package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.service.EmailService;
import com.ecommerce.Ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, String> otpStore = new ConcurrentHashMap<>(); // Lưu OTP tạm thời
    // Kiểm tra trạng thái đăng nhập

    @GetMapping("/api/check-login")
    public ResponseEntity<?> checkLogin(HttpSession session) {
        // Kiểm tra xem có user trong session hay không
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Nếu không có user, trả về false
            return ResponseEntity.ok(false);
        }
        // Nếu có user, trả về true
        return ResponseEntity.ok(true);
    }
    // Xem user đang login

    @GetMapping("/api/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Hàm tạo OTP ngẫu nhiên (6 chữ số)
     */
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * Endpoint gửi OTP qua email khi người dùng quên mật khẩu
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            // Kiểm tra xem email có tồn tại trong hệ thống không
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email này.");
            }

            // Tạo OTP ngẫu nhiên
            String otp = generateOtp();
            System.out.println("Generated OTP: " + otp); // In OTP ra console để kiểm tra
            System.out.println("Received email: " + email);
            // Lưu OTP vào bộ nhớ tạm thời (với thời gian sống 5 phút)
            otpStore.put(email, otp);

            // Gửi OTP qua email
            emailService.sendEmail(
                    email,
                    "OTP để đặt lại mật khẩu",
                    "Mã OTP của bạn là: " + otp + ". Vui lòng không chia sẻ mã này với người khác.");

            return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể gửi OTP.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        try {
            // Lấy OTP từ bộ nhớ tạm thời
            String storedOtp = otpStore.get(email);
            if (storedOtp == null || !storedOtp.equals(otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
            }

            // Tìm người dùng theo email
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này."));

            // Chỉ cập nhật trường mật khẩu
            User updatedUser = new User();
            updatedUser.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa mật khẩu mới

            // Gọi phương thức updateUser để cập nhật mật khẩu
            userService.updateUser(user.getId(), updatedUser);

            // Xóa OTP khỏi bộ nhớ tạm thời (chỉ xóa sau khi cập nhật mật khẩu thành công)
            otpStore.remove(email);

            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đặt lại mật khẩu.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        try {
            String storedOtp = otpStore.get(email);
            if (storedOtp == null || !storedOtp.equals(otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
            }

            // Không xóa OTP ở đây
            return ResponseEntity.ok("OTP hợp lệ.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xác thực OTP.");
        }
    }

    // Đăng xuất
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // Xóa thông tin người dùng khỏi session
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    // Các endpoint CRUD cho người dùng không đăng nhập qua Google tại /user
    /**
     * Lấy danh sách tất cả người dùng
     */
    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
 
    /**
     * Lấy thông tin người dùng theo ID
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cập nhật thông tin người dùng
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Kiểm tra thông tin đăng nhập
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        User user = optionalUser.get(); // Lấy đối tượng User từ Optional

        // Lưu thông tin người dùng vào session
        session.setAttribute("user", user);

        // Trả về thông tin người dùng
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa người dùng
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpont đăng nhập không qua Gg
     * 
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println("Received user data: " + user);
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (IllegalArgumentException e) {
            try {
                // Lấy thông báo lỗi từ nguyên nhân của ngoại lệ
                String jsonErrors = e.getCause().getMessage();
                Map<String, String> errors = new ObjectMapper().readValue(jsonErrors,
                        new TypeReference<Map<String, String>>() {
                        });
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            } catch (Exception jsonError) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký.");
        }
    }
}

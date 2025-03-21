// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.model.AdminRole;
// import com.ecommerce.Ecommerce.model.Google;
// import com.ecommerce.Ecommerce.model.User;
// import com.ecommerce.Ecommerce.repository.AdminRoleRepository;
// import com.ecommerce.Ecommerce.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.regex.Pattern;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

// @Service
// public class UserService {
//     @Autowired
//     private AdminRoleRepository adminRoleRepository;
//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private EmailService emailService;

//     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//     // Regex patterns
//     private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
//     private static final String PASSWORD_REGEX = "^.{6,}$";
//     private static final String PHONE_REGEX = "^[0-9]{10,15}$";

//     private Map<String, String> validateUserData(User user) {
//         System.out.println("Validating user data...");
//         Map<String, String> errorMessages = new HashMap<>();

//         if (user.getEmail() == null || !Pattern.matches(EMAIL_REGEX, user.getEmail())) {
//             errorMessages.put("email", "Email không hợp lệ.");
//         }
//         if (user.getPassword() == null || !Pattern.matches(PASSWORD_REGEX, user.getPassword())) {
//             errorMessages.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
//         }
//         if (user.getPhone() != null && !Pattern.matches(PHONE_REGEX, user.getPhone())) {
//             errorMessages.put("phone", "Số điện thoại không hợp lệ.");
//         }
//         if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
//             errorMessages.put("firstName", "Tên không được để trống.");
//         }
//         if (user.getLastName() == null || user.getLastName().isEmpty()) {
//             errorMessages.put("lastName", "Họ không được để trống.");
//         }

//         // In ra errorMessages để kiểm tra
//         System.out.println("Error messages: " + errorMessages);

//         return errorMessages; // Trả về danh sách lỗi
//     }

//     /**
//      * Tạo mới người dùng từ thông tin Google
//      */
//     public User createUserFromGoogle(String email, String name, String googleId, String idToken, String accessToken,
//             Boolean emailVerified) {
//         User newUser = new User();
//         newUser.setEmail(email);
//         newUser.setFirstName(name);
//         newUser.setRole("user"); // Mặc định gán role "user"
//         newUser.setIsActive(true);
//         Google google = new Google();
//         google.setGoogleId(googleId);
//         google.setIdToken(idToken);
//         google.setAccessToken(accessToken);
//         google.setEmailVerified(emailVerified);
//         newUser.setGoogle(google);

//         return userRepository.save(newUser); // Lưu người dùng mới vào CSDL
//     }

//     public Optional<User> getUserByGoogleId(String googleId) {
//         return userRepository.findByGoogle_GoogleId(googleId);
//     }

//     /**
//      * Lấy danh sách tất cả người dùng
//      */
//     public List<User> getAllUsers() {
//         return userRepository.findAll();
//     }

//     /**
//      * Lấy thông tin người dùng theo ID
//      */
//     public Optional<User> getUserById(Long id) {
//         return userRepository.findById(id);
//     }

//     public Optional<User> findByEmail(String email) {
//         return userRepository.findByEmail(email);
//     }

//     // Đăng ksy user
//     public User registerUser(User user) {
//         Map<String, String> errors = validateUserData(user);
//         if (!errors.isEmpty()) {
//             try {
//                 String jsonErrors = new ObjectMapper().writeValueAsString(errors);
//                 throw new IllegalArgumentException("Dữ liệu không hợp lệ", new Throwable(jsonErrors));
//             } catch (JsonProcessingException e) {
//                 // Xử lý lỗi chuyển đổi JSON
//                 System.out.println("Error while converting to JSON: " + e.getMessage());
//                 throw new IllegalArgumentException("Lỗi khi xử lý thông báo lỗi.");
//             }
//         }

//         // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         user.setRole("user"); // Gán vai trò mặc định
//         user.setIsActive(true); // Kích hoạt tài khoản

//         // Lưu người dùng vào cơ sở dữ liệu
//         User savedUser = userRepository.save(user);

//         // Gửi email thông báo
//         emailService.sendEmail(
//                 savedUser.getEmail(),
//                 "Đăng ký tài khoản thành công",
//                 "Chào mừng đến với LITTLEUSA");

//         return savedUser;
//     }

//     public Optional<User> findByUsername(String username) {
//         return userRepository.findByUsername(username);
//     }

//     /**
//      * Cập nhật thông tin người dùng
//      */
//     public User updateUser(Long id, User updatedUser) {
//         return userRepository.findById(id).map(user -> {
//             if (updatedUser.getUsername() != null)
//                 user.setUsername(updatedUser.getUsername());
//             if (updatedUser.getEmail() != null)
//                 user.setEmail(updatedUser.getEmail());
//             if (updatedUser.getFirstName() != null)
//                 user.setFirstName(updatedUser.getFirstName());
//             if (updatedUser.getLastName() != null)
//                 user.setLastName(updatedUser.getLastName());
//             if (updatedUser.getPhone() != null)
//                 user.setPhone(updatedUser.getPhone());
//             if (updatedUser.getAddress() != null)
//                 user.setAddress(updatedUser.getAddress());
//             if (updatedUser.getPassword() != null)
//                 user.setPassword(updatedUser.getPassword()); // Cập nhật mật khẩu
//             if (updatedUser.getRole() != null)
//                 user.setRole(updatedUser.getRole());
//             if (updatedUser.getIsActive() != null)
//                 user.setIsActive(updatedUser.getIsActive());
//             return userRepository.save(user);
//         }).orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với ID: " + id));
//     }

//     /**
//      * Xóa người dùng theo ID
//      */
//     public void deleteUser(Long id) {
//         if (!userRepository.existsById(id)) {
//             throw new IllegalArgumentException("Người dùng không tồn tại với ID: " + id);
//         }
//         userRepository.deleteById(id);
//     }
//     public List<AdminRole> getAdminRolesByUserId(Long userId) {
//         return adminRoleRepository.findByUserId(userId);
//     }

//     // Kiểm tra xem user có vai trò super_admin hoặc product_manager không
//     public boolean hasRequiredAdminRole(User user) {
//         List<AdminRole> adminRoles = getAdminRolesByUserId(user.getId());
//         return adminRoles.stream().anyMatch(role -> 
//             role.getAdminRole().equals("super_admin") || role.getAdminRole().equals("product_manager"));
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.AdminRole;
import com.ecommerce.Ecommerce.model.Google;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.repository.AdminRoleRepository;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Regex patterns
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^.{6,}$";
    private static final String PHONE_REGEX = "^[0-9]{10,15}$";

    private Map<String, String> validateUserData(User user) {
        System.out.println("Validating user data...");
        Map<String, String> errorMessages = new HashMap<>();

        if (user.getEmail() == null || !Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errorMessages.put("email", "Email không hợp lệ.");
        }
        if (user.getPassword() == null || !Pattern.matches(PASSWORD_REGEX, user.getPassword())) {
            errorMessages.put("password", "Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (user.getPhone() != null && !Pattern.matches(PHONE_REGEX, user.getPhone())) {
            errorMessages.put("phone", "Số điện thoại không hợp lệ.");
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            errorMessages.put("firstName", "Tên không được để trống.");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            errorMessages.put("lastName", "Họ không được để trống.");
        }

        // In ra errorMessages để kiểm tra
        System.out.println("Error messages: " + errorMessages);

        return errorMessages; // Trả về danh sách lỗi
    }

    /**
     * Tạo mới người dùng từ thông tin Google
     */
    public User createUserFromGoogle(String email, String name, String googleId, String idToken, String accessToken,
            Boolean emailVerified) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName(name);
        newUser.setRole("user"); // Mặc định gán role "user"
        newUser.setIsActive(true);
        Google google = new Google();
        google.setGoogleId(googleId);
        google.setIdToken(idToken);
        google.setAccessToken(accessToken);
        google.setEmailVerified(emailVerified);
        newUser.setGoogle(google);

        return userRepository.save(newUser); // Lưu người dùng mới vào CSDL
    }

    public Optional<User> getUserByGoogleId(String googleId) {
        return userRepository.findByGoogle_GoogleId(googleId);
    }

    /**
     * Lấy danh sách tất cả người dùng
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Lấy thông tin người dùng theo ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Đăng ký user
    public User registerUser(User user) {
        Map<String, String> errors = validateUserData(user);
        if (!errors.isEmpty()) {
            try {
                String jsonErrors = new ObjectMapper().writeValueAsString(errors);
                throw new IllegalArgumentException("Dữ liệu không hợp lệ", new Throwable(jsonErrors));
            } catch (JsonProcessingException e) {
                // Xử lý lỗi chuyển đổi JSON
                System.out.println("Error while converting to JSON: " + e.getMessage());
                throw new IllegalArgumentException("Lỗi khi xử lý thông báo lỗi.");
            }
        }

        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user"); // Gán vai trò mặc định
        user.setIsActive(true); // Kích hoạt tài khoản

        // Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepository.save(user);

        // Gửi email thông báo
        emailService.sendEmail(
                savedUser.getEmail(),
                "Đăng ký tài khoản thành công",
                "Chào mừng đến với LITTLEUSA");

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Cập nhật thông tin người dùng
     */
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            if (updatedUser.getUsername() != null)
                user.setUsername(updatedUser.getUsername());
            if (updatedUser.getEmail() != null)
                user.setEmail(updatedUser.getEmail());
            if (updatedUser.getFirstName() != null)
                user.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null)
                user.setLastName(updatedUser.getLastName());
            if (updatedUser.getPhone() != null)
                user.setPhone(updatedUser.getPhone());
            if (updatedUser.getAddress() != null)
                user.setAddress(updatedUser.getAddress());
            if (updatedUser.getPassword() != null)
                user.setPassword(updatedUser.getPassword()); // Cập nhật mật khẩu
            if (updatedUser.getRole() != null)
                user.setRole(updatedUser.getRole());
            if (updatedUser.getIsActive() != null)
                user.setIsActive(updatedUser.getIsActive());
            return userRepository.save(user);
        }).orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với ID: " + id));
    }

    /**
     * Xóa người dùng theo ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Người dùng không tồn tại với ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<AdminRole> getAdminRolesByUserId(Long userId) {
        return adminRoleRepository.findByUserId(userId);
    }

    /**
     * Kiểm tra xem user có một trong các vai trò admin yêu cầu không
     * @param user Người dùng cần kiểm tra
     * @param requiredRoles Mảng các vai trò admin yêu cầu
     * @return true nếu user có ít nhất một vai trò trong requiredRoles, false nếu không
     */
    public boolean hasRequiredAdminRole(User user, String[] requiredRoles) {
        List<AdminRole> adminRoles = getAdminRolesByUserId(user.getId());
        System.out.println("Admin roles for user " + user.getId() + ": " + adminRoles);
        boolean hasRole = adminRoles.stream().anyMatch(role -> 
            Arrays.asList(requiredRoles).contains(role.getAdminRole()));
        System.out.println("Required roles: " + Arrays.toString(requiredRoles));
        System.out.println("Has required admin role: " + hasRole);
        return hasRole;
    }


   
}

// // package com.ecommerce.Ecommerce.controller;

// // import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
// // import com.ecommerce.Ecommerce.annotation.RequireUserRole;
// // import com.ecommerce.Ecommerce.model.AdminRole;
// // import com.ecommerce.Ecommerce.model.User;
// // import com.ecommerce.Ecommerce.service.EmailService;
// // import com.ecommerce.Ecommerce.service.UserService;
// // import com.fasterxml.jackson.databind.ObjectMapper;

// // import jakarta.servlet.http.HttpSession;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;

// // import java.time.LocalDateTime;
// // import java.util.HashMap;
// // import java.util.List;
// // import java.util.Map;
// // import java.util.Optional;
// // import java.util.Random;
// // import java.util.concurrent.ConcurrentHashMap;
// // import java.util.stream.Collectors;

// // import com.fasterxml.jackson.core.type.TypeReference;
// // import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// // @RestController
// // public class UserController {

// //     @Autowired
// //     private UserService userService;

// //     @Autowired
// //     private EmailService emailService;

// //     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
// //     private final Map<String, String> otpStore = new ConcurrentHashMap<>();

// //     // Định nghĩa static inner class AdminDTO
// //     private static class AdminDTO {
// //         private Long userId;
// //         private String firstName;
// //         private String lastName;
// //         private String phone;
// //         private String username;
// //         private String password;
// //         private List<String> adminRoles;
// //         private LocalDateTime createdAt;

// //         public AdminDTO(Long userId, String firstName, String lastName, String phone, String username, String password, List<String> adminRoles, LocalDateTime createdAt) {
// //             this.userId = userId;
// //             this.firstName = firstName;
// //             this.lastName = lastName;
// //             this.phone = phone;
// //             this.username = username;
// //             this.password = password;
// //             this.adminRoles = adminRoles;
// //             this.createdAt = createdAt;
// //         }

// //         // Getters
// //         public Long getUserId() {
// //             return userId;
// //         }

// //         public String getFirstName() {
// //             return firstName;
// //         }

// //         public String getLastName() {
// //             return lastName;
// //         }

// //         public String getPhone() {
// //             return phone;
// //         }

// //         public String getUsername() {
// //             return username;
// //         }

// //         public String getPassword() {
// //             return password;
// //         }

// //         public List<String> getAdminRoles() {
// //             return adminRoles;
// //         }

// //         public LocalDateTime getCreatedAt() {
// //             return createdAt;
// //         }
// //     }

// //     // Kiểm tra trạng thái đăng nhập
// //     @GetMapping("/api/check-login")
// //     public ResponseEntity<?> checkLogin(HttpSession session) {
// //         User user = (User) session.getAttribute("user");
// //         if (user == null) {
// //             return ResponseEntity.ok(false);
// //         }
// //         return ResponseEntity.ok(true);
// //     }

// //     // Xem user đang login
// //     @GetMapping("/api/current-user")
// //     public ResponseEntity<?> getCurrentUser(HttpSession session) {
// //         User user = (User) session.getAttribute("user");
// //         if (user == null) {
// //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
// //         }
// //         return ResponseEntity.ok(user);
// //     }

// //     private String generateOtp() {
// //         return String.format("%06d", new Random().nextInt(999999));
// //     }

// //     @PostMapping("/forgot-password")
// //     public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
// //         String email = request.get("email");

// //         try {
// //             Optional<User> optionalUser = userService.findByEmail(email);
// //             if (optionalUser.isEmpty()) {
// //                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email này.");
// //             }

// //             String otp = generateOtp();
// //             System.out.println("Generated OTP: " + otp);
// //             System.out.println("Received email: " + email);
// //             otpStore.put(email, otp);

// //             emailService.sendEmail(
// //                     email,
// //                     "OTP để đặt lại mật khẩu",
// //                     "Mã OTP của bạn là: " + otp + ". Vui lòng không chia sẻ mã này với người khác.");

// //             return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
// //         } catch (Exception e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể gửi OTP.");
// //         }
// //     }

// //     @PostMapping("/reset-password")
// //     public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
// //         String email = request.get("email");
// //         String otp = request.get("otp");
// //         String newPassword = request.get("newPassword");

// //         try {
// //             String storedOtp = otpStore.get(email);
// //             if (storedOtp == null || !storedOtp.equals(otp)) {
// //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
// //             }

// //             User user = userService.findByEmail(email)
// //                     .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này."));

// //             User updatedUser = new User();
// //             updatedUser.setPassword(passwordEncoder.encode(newPassword));
// //             userService.updateUser(user.getId(), updatedUser);

// //             otpStore.remove(email);
// //             return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
// //         } catch (Exception e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đặt lại mật khẩu.");
// //         }
// //     }

// //     @PostMapping("/verify-otp")
// //     public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
// //         String email = request.get("email");
// //         String otp = request.get("otp");

// //         try {
// //             String storedOtp = otpStore.get(email);
// //             if (storedOtp == null || !storedOtp.equals(otp)) {
// //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
// //             }
// //             return ResponseEntity.ok("OTP hợp lệ.");
// //         } catch (Exception e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xác thực OTP.");
// //         }
// //     }

// //     @PostMapping("/api/logout")
// //     public ResponseEntity<?> logout(HttpSession session) {
// //         session.invalidate();
// //         return ResponseEntity.ok("Logged out successfully");
// //     }

// //     @GetMapping("/user")
// //     public ResponseEntity<List<User>> getAllUsers() {
// //         return ResponseEntity.ok(userService.getAllUsers());
// //     }

// //     @GetMapping("/user/{id}")
// //     public ResponseEntity<User> getUserById(@PathVariable Long id) {
// //         Optional<User> user = userService.getUserById(id);
// //         return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
// //     }

// //     @PutMapping("/user/{id}")
// //     @RequireUserRole // Cho phép user có role "user" cập nhật chính mình
// //     @RequireAdminRole(roles = {"super_admin"}) // Chỉ super_admin trong số các admin được phép
// //     public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
// //         try {
// //             User user = userService.updateUser(id, updatedUser);
// //             return ResponseEntity.ok(user);
// //         } catch (IllegalArgumentException e) {
// //             return ResponseEntity.notFound().build();
// //         }
// //     }

// //     @PostMapping("/login")
// //     public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
// //         String username = credentials.get("username");
// //         String password = credentials.get("password");

// //         Optional<User> optionalUser = userService.findByUsername(username);
// //         if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
// //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng.");
// //         }

// //         User user = optionalUser.get();
// //         session.setAttribute("user", user);

// //         Map<String, Object> response = new HashMap<>();
// //         response.put("user", user);
// //         return ResponseEntity.ok(response);
// //     }

// //     @DeleteMapping("/user/{id}")
// //     @RequireAdminRole(roles = {"super_admin"})
// //     public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
// //         try {
// //             userService.deleteUser(id);
// //             return ResponseEntity.noContent().build();
// //         } catch (IllegalArgumentException e) {
// //             return ResponseEntity.notFound().build();
// //         }
// //     }

// //     @PostMapping("/register")
// //     public ResponseEntity<?> registerUser(@RequestBody User user) {
// //         System.out.println("Received user data: " + user);
// //         try {
// //             userService.registerUser(user);
// //             return ResponseEntity.ok("Đăng ký thành công!");
// //         } catch (IllegalArgumentException e) {
// //             try {
// //                 String jsonErrors = e.getCause().getMessage();
// //                 Map<String, String> errors = new ObjectMapper().readValue(jsonErrors,
// //                         new TypeReference<Map<String, String>>() {});
// //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
// //             } catch (Exception jsonError) {
// //                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
// //             }
// //         } catch (Exception e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký.");
// //         }
// //     }

// //     /**
// //      * Endpoint trả về danh sách admin với các thông tin: userId, firstName, lastName, phone, username, password, admin_role, createdAt
// //      * Chỉ super_admin mới được phép truy cập
// //      */
// //     @GetMapping("/api/admins")
// //     @RequireAdminRole(roles = {"super_admin"})
// //     public ResponseEntity<List<AdminDTO>> getAllAdmins() {
// //         try {
// //             // Lấy danh sách tất cả người dùng có role là "admin"
// //             List<User> admins = userService.getAllUsers().stream()
// //                     .filter(user -> "admin".equals(user.getRole()))
// //                     .collect(Collectors.toList());

// //             // Chuyển đổi danh sách người dùng thành danh sách AdminDTO
// //             List<AdminDTO> adminDTOs = admins.stream().map(user -> {
// //                 // Lấy danh sách vai trò admin từ bảng admin_roles
// //                 List<AdminRole> adminRoles = userService.getAdminRolesByUserId(user.getId());
// //                 List<String> roles = adminRoles.stream()
// //                         .map(AdminRole::getAdminRole)
// //                         .collect(Collectors.toList());

// //                 // Tạo AdminDTO
// //                 return new AdminDTO(
// //                         user.getId(),
// //                         user.getFirstName(),
// //                         user.getLastName(),
// //                         user.getPhone(),
// //                         user.getUsername(),
// //                         user.getPassword(),
// //                         roles,
// //                         user.getCreatedAt()
// //                 );
// //             }).collect(Collectors.toList());

// //             return ResponseEntity.ok(adminDTOs);
// //         } catch (Exception e) {
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
// //         }
// //     }
// //     // Trong UserController.java

// // @PostMapping("/api/change-password")
// // @RequireAdminRole(roles = {"super_admin"}) // Chỉ super_admin được đổi mật khẩu
// // public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
// //     String oldPassword = request.get("oldPassword");
// //     String newPassword = request.get("newPassword");

// //     try {
// //         // Lấy thông tin user từ session
// //         User currentUser = (User) session.getAttribute("user");
// //         if (currentUser == null) {
// //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để đổi mật khẩu.");
// //         }

// //         // Kiểm tra mật khẩu cũ
// //         if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ không đúng.");
// //         }

// //         // Cập nhật mật khẩu mới
// //         User updatedUser = new User();
// //         updatedUser.setPassword(passwordEncoder.encode(newPassword));
// //         userService.updateUser(currentUser.getId(), updatedUser);

// //         return ResponseEntity.ok("Đổi mật khẩu thành công.");
// //     } catch (Exception e) {
// //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đổi mật khẩu.");
// //     }
// // }
// // }
// package com.ecommerce.Ecommerce.controller;

// import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
// import com.ecommerce.Ecommerce.annotation.RequireUserRole;
// import com.ecommerce.Ecommerce.model.AdminRole;
// import com.ecommerce.Ecommerce.model.User;
// import com.ecommerce.Ecommerce.service.EmailService;
// import com.ecommerce.Ecommerce.service.UserService;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.servlet.http.HttpSession;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Random;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.stream.Collectors;

// import com.fasterxml.jackson.core.type.TypeReference;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// @RestController
// public class UserController {

//     @Autowired
//     private UserService userService;

//     @Autowired
//     private EmailService emailService;

//     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//     private final Map<String, String> otpStore = new ConcurrentHashMap<>();

//     // Định nghĩa static inner class AdminDTO
//     private static class AdminDTO {
//         private Long userId;
//         private String firstName;
//         private String lastName;
//         private String phone;
//         private String username;
//         private String password;
//         private List<String> adminRoles;
//         private LocalDateTime createdAt;

//         public AdminDTO(Long userId, String firstName, String lastName, String phone, String username, String password, List<String> adminRoles, LocalDateTime createdAt) {
//             this.userId = userId;
//             this.firstName = firstName;
//             this.lastName = lastName;
//             this.phone = phone;
//             this.username = username;
//             this.password = password;
//             this.adminRoles = adminRoles;
//             this.createdAt = createdAt;
//         }

//         // Getters
//         public Long getUserId() {
//             return userId;
//         }

//         public String getFirstName() {
//             return firstName;
//         }

//         public String getLastName() {
//             return lastName;
//         }

//         public String getPhone() {
//             return phone;
//         }

//         public String getUsername() {
//             return username;
//         }

//         public String getPassword() {
//             return password;
//         }

//         public List<String> getAdminRoles() {
//             return adminRoles;
//         }

//         public LocalDateTime getCreatedAt() {
//             return createdAt;
//         }
//     }

//     // Định nghĩa DTO mới cho danh sách admin (đã thêm password)
//     private static class AdminListDTO {
//         private String firstName;      // Tên
//         private String lastName;       // Họ
//         private String address;        // Địa chỉ (kết hợp các trường của Address)
//         private String username;       // Tên đăng nhập
//         private String password;       // Mật khẩu (đã thêm)
//         private List<String> adminRoles; // Danh sách các vai trò admin
//         private String phone;          // Số điện thoại
//         private String email;          // Email
//         private LocalDateTime createdAt; // Ngày tạo

//         // Constructor
//         public AdminListDTO(String firstName, String lastName, String address, String username, 
//                            String password, List<String> adminRoles, String phone, String email, LocalDateTime createdAt) {
//             this.firstName = firstName;
//             this.lastName = lastName;
//             this.address = address;
//             this.username = username;
//             this.password = password;
//             this.adminRoles = adminRoles;
//             this.phone = phone;
//             this.email = email;
//             this.createdAt = createdAt;
//         }

//         // Getters
//         public String getFirstName() {
//             return firstName;
//         }

//         public String getLastName() {
//             return lastName;
//         }

//         public String getAddress() {
//             return address;
//         }

//         public String getUsername() {
//             return username;
//         }

//         public String getPassword() {
//             return password;
//         }

//         public List<String> getAdminRoles() {
//             return adminRoles;
//         }

//         public String getPhone() {
//             return phone;
//         }

//         public String getEmail() {
//             return email;
//         }

//         public LocalDateTime getCreatedAt() {
//             return createdAt;
//         }
//     }

//     // Kiểm tra trạng thái đăng nhập
//     @GetMapping("/api/check-login")
//     public ResponseEntity<?> checkLogin(HttpSession session) {
//         User user = (User) session.getAttribute("user");
//         if (user == null) {
//             return ResponseEntity.ok(false);
//         }
//         return ResponseEntity.ok(true);
//     }

//     // Xem user đang login
//     @GetMapping("/api/current-user")
//     public ResponseEntity<?> getCurrentUser(HttpSession session) {
//         User user = (User) session.getAttribute("user");
//         if (user == null) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//         }
//         return ResponseEntity.ok(user);
//     }

//     private String generateOtp() {
//         return String.format("%06d", new Random().nextInt(999999));
//     }

//     @PostMapping("/forgot-password")
//     public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
//         String email = request.get("email");

//         try {
//             Optional<User> optionalUser = userService.findByEmail(email);
//             if (optionalUser.isEmpty()) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email này.");
//             }

//             String otp = generateOtp();
//             System.out.println("Generated OTP: " + otp);
//             System.out.println("Received email: " + email);
//             otpStore.put(email, otp);

//             emailService.sendEmail(
//                     email,
//                     "OTP để đặt lại mật khẩu",
//                     "Mã OTP của bạn là: " + otp + ". Vui lòng không chia sẻ mã này với người khác.");

//             return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể gửi OTP.");
//         }
//     }

//     @PostMapping("/reset-password")
//     public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
//         String email = request.get("email");
//         String otp = request.get("otp");
//         String newPassword = request.get("newPassword");

//         try {
//             String storedOtp = otpStore.get(email);
//             if (storedOtp == null || !storedOtp.equals(otp)) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
//             }

//             User user = userService.findByEmail(email)
//                     .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này."));

//             User updatedUser = new User();
//             updatedUser.setPassword(passwordEncoder.encode(newPassword));
//             userService.updateUser(user.getId(), updatedUser);

//             otpStore.remove(email);
//             return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đặt lại mật khẩu.");
//         }
//     }

//     @PostMapping("/verify-otp")
//     public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
//         String email = request.get("email");
//         String otp = request.get("otp");

//         try {
//             String storedOtp = otpStore.get(email);
//             if (storedOtp == null || !storedOtp.equals(otp)) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
//             }
//             return ResponseEntity.ok("OTP hợp lệ.");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xác thực OTP.");
//         }
//     }

//     @PostMapping("/api/logout")
//     public ResponseEntity<?> logout(HttpSession session) {
//         session.invalidate();
//         return ResponseEntity.ok("Logged out successfully");
//     }

//     @GetMapping("/user")
//     public ResponseEntity<List<User>> getAllUsers() {
//         return ResponseEntity.ok(userService.getAllUsers());
//     }

//     @GetMapping("/user/{id}")
//     public ResponseEntity<User> getUserById(@PathVariable Long id) {
//         Optional<User> user = userService.getUserById(id);
//         return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//     }

//     @PutMapping("/user/{id}")
//     @RequireUserRole // Cho phép user có role "user" cập nhật chính mình
//     @RequireAdminRole(roles = {"super_admin"}) // Chỉ super_admin trong số các admin được phép
//     public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
//         try {
//             User user = userService.updateUser(id, updatedUser);
//             return ResponseEntity.ok(user);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
//         String username = credentials.get("username");
//         String password = credentials.get("password");

//         Optional<User> optionalUser = userService.findByUsername(username);
//         if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng.");
//         }

//         User user = optionalUser.get();
//         session.setAttribute("user", user);

//         Map<String, Object> response = new HashMap<>();
//         response.put("user", user);
//         return ResponseEntity.ok(response);
//     }

//     @DeleteMapping("/user/{id}")
//     @RequireAdminRole(roles = {"super_admin"})
//     public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//         try {
//             userService.deleteUser(id);
//             return ResponseEntity.noContent().build();
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.notFound().build();
//         }
//     }

//     @PostMapping("/register")
//     public ResponseEntity<?> registerUser(@RequestBody User user) {
//         System.out.println("Received user data: " + user);
//         try {
//             userService.registerUser(user);
//             return ResponseEntity.ok("Đăng ký thành công!");
//         } catch (IllegalArgumentException e) {
//             try {
//                 String jsonErrors = e.getCause().getMessage();
//                 Map<String, String> errors = new ObjectMapper().readValue(jsonErrors,
//                         new TypeReference<Map<String, String>>() {});
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//             } catch (Exception jsonError) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
//             }
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký.");
//         }
//     }

//     /**
//      * Endpoint trả về danh sách admin với các thông tin: userId, firstName, lastName, phone, username, password, admin_role, createdAt
//      * Chỉ super_admin mới được phép truy cập
//      */
//     @GetMapping("/api/admins")
//     @RequireAdminRole(roles = {"super_admin"})
//     public ResponseEntity<List<AdminDTO>> getAllAdmins() {
//         try {
//             // Lấy danh sách tất cả người dùng có role là "admin"
//             List<User> admins = userService.getAllUsers().stream()
//                     .filter(user -> "admin".equals(user.getRole()))
//                     .collect(Collectors.toList());

//             // Chuyển đổi danh sách người dùng thành danh sách AdminDTO
//             List<AdminDTO> adminDTOs = admins.stream().map(user -> {
//                 // Lấy danh sách vai trò admin từ bảng admin_roles
//                 List<AdminRole> adminRoles = userService.getAdminRolesByUserId(user.getId());
//                 List<String> roles = adminRoles.stream()
//                         .map(AdminRole::getAdminRole)
//                         .collect(Collectors.toList());

//                 // Tạo AdminDTO
//                 return new AdminDTO(
//                         user.getId(),
//                         user.getFirstName(),
//                         user.getLastName(),
//                         user.getPhone(),
//                         user.getUsername(),
//                         user.getPassword(),
//                         roles,
//                         user.getCreatedAt()
//                 );
//             }).collect(Collectors.toList());

//             return ResponseEntity.ok(adminDTOs);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//         }
//     }

//     /**
//      * Endpoint trả về danh sách admin với thông tin: họ, tên, địa chỉ, tên đăng nhập, mật khẩu, role admin, số điện thoại, email, createdAt
//      * Chỉ super_admin mới được phép truy cập
//      */
//     @GetMapping("/api/admin-list")
//     @RequireAdminRole(roles = {"super_admin"})
//     public ResponseEntity<List<AdminListDTO>> getAdminList() {
//         try {
//             // Lấy danh sách tất cả người dùng có role là "admin"
//             List<User> admins = userService.getAllUsers().stream()
//                     .filter(user -> "admin".equals(user.getRole()))
//                     .collect(Collectors.toList());

//             // Chuyển đổi danh sách admin thành danh sách AdminListDTO
//             List<AdminListDTO> adminListDTOs = admins.stream().map(user -> {
//                 // Lấy danh sách vai trò admin từ bảng admin_roles
//                 List<AdminRole> adminRoles = userService.getAdminRolesByUserId(user.getId());
//                 List<String> roles = adminRoles.stream()
//                         .map(AdminRole::getAdminRole)
//                         .collect(Collectors.toList());

//                 // Tạo chuỗi địa chỉ từ đối tượng Address (nếu có)
//                 String addressString = user.getAddress() != null 
//                     ? String.format("%s, %s, %s, %s, %s", 
//                         user.getAddress().getStreet() != null ? user.getAddress().getStreet() : "", 
//                         user.getAddress().getWard() != null ? user.getAddress().getWard() : "", 
//                         user.getAddress().getDistrict() != null ? user.getAddress().getDistrict() : "", 
//                         user.getAddress().getCity() != null ? user.getAddress().getCity() : "", 
//                         user.getAddress().getCountry() != null ? user.getAddress().getCountry() : "")
//                     .replaceAll(",\\s*,", ",").replaceAll("^,\\s*|\\s*,$", "") // Loại bỏ dấu phẩy thừa
//                     : "N/A";

//                 // Tạo AdminListDTO
//                 return new AdminListDTO(
//                     user.getFirstName(),
//                     user.getLastName(),
//                     addressString,
//                     user.getUsername(),
//                     user.getPassword(), // Thêm trường password
//                     roles,
//                     user.getPhone(),
//                     user.getEmail(),
//                     user.getCreatedAt()
//                 );
//             }).collect(Collectors.toList());

//             return ResponseEntity.ok(adminListDTOs);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//         }
//     }

//     @PostMapping("/api/change-password")
//     @RequireAdminRole(roles = {"super_admin"}) // Chỉ super_admin được đổi mật khẩu
//     public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
//         String oldPassword = request.get("oldPassword");
//         String newPassword = request.get("newPassword");

//         try {
//             // Lấy thông tin user từ session
//             User currentUser = (User) session.getAttribute("user");
//             if (currentUser == null) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để đổi mật khẩu.");
//             }

//             // Kiểm tra mật khẩu cũ
//             if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ không đúng.");
//             }

//             // Cập nhật mật khẩu mới
//             User updatedUser = new User();
//             updatedUser.setPassword(passwordEncoder.encode(newPassword));
//             userService.updateUser(currentUser.getId(), updatedUser);

//             return ResponseEntity.ok("Đổi mật khẩu thành công.");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đổi mật khẩu.");
//         }
//     }
// }
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.annotation.RequireUserRole;
import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.AdminRole;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.model.Address;
import com.ecommerce.Ecommerce.service.EmailService;
import com.ecommerce.Ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    private static class AdminDTO {
        private Long userId;
        private String firstName;
        private String lastName;
        private String phone;
        private String username;
        private String password;
        private List<String> adminRoles;
        private LocalDateTime createdAt;

        public AdminDTO(Long userId, String firstName, String lastName, String phone, String username, String password, List<String> adminRoles, LocalDateTime createdAt) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.username = username;
            this.password = password;
            this.adminRoles = adminRoles;
            this.createdAt = createdAt;
        }

        public Long getUserId() { return userId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public List<String> getAdminRoles() { return adminRoles; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    private static class AdminListDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private Address address;
        private String username;
        private String password;
        private List<String> adminRoles;
        private String phone;
        private String email;
        private LocalDateTime createdAt;

        public AdminListDTO(Long id, String firstName, String lastName, Address address, String username, 
                           String password, List<String> adminRoles, String phone, String email, LocalDateTime createdAt) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.username = username;
            this.password = password;
            this.adminRoles = adminRoles;
            this.phone = phone;
            this.email = email;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public Address getAddress() { return address; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public List<String> getAdminRoles() { return adminRoles; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    @GetMapping("/api/check-login")
    public ResponseEntity<?> checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email này.");
            }
            String otp = generateOtp();
            System.out.println("Generated OTP: " + otp);
            System.out.println("Received email: " + email);
            otpStore.put(email, otp);
            emailService.sendEmail(email, "OTP để đặt lại mật khẩu", "Mã OTP của bạn là: " + otp + ". Vui lòng không chia sẻ mã này với người khác.");
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
            String storedOtp = otpStore.get(email);
            if (storedOtp == null || !storedOtp.equals(otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
            }
    
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này."));
    
            // In ra mật khẩu người dùng nhập trước khi mã hóa
            System.out.println("Mật khẩu người dùng nhập: " + newPassword);
            String encodedPassword = passwordEncoder.encode(newPassword);
            // In ra mật khẩu mới được mã hóa
            System.out.println("Mật khẩu mới được mã hóa: " + encodedPassword);
            // In ra thông tin user id
            System.out.println("Đặt lại mật khẩu cho user id: " + user.getId());
    
            // Cập nhật trực tiếp mật khẩu cho user hiện tại
            user.setPassword(encodedPassword);
            User updatedUser = userService.updateUser(user.getId(), user);
            // In ra mật khẩu đã lưu trong đối tượng sau khi cập nhật
            System.out.println("Mật khẩu đã lưu cho user id " + updatedUser.getId() + ": " + updatedUser.getPassword());
    
            otpStore.remove(email);
            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            System.out.println("Lỗi khi đặt lại mật khẩu: " + e.getMessage());
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
            return ResponseEntity.ok("OTP hợp lệ.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xác thực OTP.");
        }
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}")
    @RequireUserRole
    @RequireAdminRole(roles = {"super_admin"})
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
        // In mật khẩu gốc người dùng vừa nhập
        System.out.println("Password entered for login: " + password);
        // In mật khẩu sau khi mã hóa (chỉ để hiển thị, không dùng để lưu)
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded password (for display only): " + encodedPassword);
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        User user = optionalUser.get();
        session.setAttribute("user", user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{id}")
    @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

@PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    System.out.println("Received user data: " + user);
    try {
        userService.registerUser(user);
        return ResponseEntity.ok("Đăng ký thành công!");
    } catch (InvalidInputException e) {
        try {
            Map<String, String> errors = new ObjectMapper().readValue(e.getMessage(),
                    new TypeReference<Map<String, String>>() {});
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } catch (Exception jsonError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi đăng ký.");
    }
}

    @GetMapping("/api/admin-list")
    // @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<List<AdminListDTO>> getAdminList() {
        try {
            List<User> admins = userService.getAllUsers().stream()
                    .filter(user -> "admin".equals(user.getRole()))
                    .collect(Collectors.toList());
            List<AdminListDTO> adminListDTOs = admins.stream().map(user -> {
                List<AdminRole> adminRoles = userService.getAdminRolesByUserId(user.getId());
                List<String> roles = adminRoles.stream()
                        .map(AdminRole::getAdminRole)
                        .collect(Collectors.toList());
                return new AdminListDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAddress(),
                    user.getUsername(),
                    user.getPassword(),
                    roles,
                    user.getPhone(),
                    user.getEmail(),
                    user.getCreatedAt()
                );
            }).collect(Collectors.toList());
            return ResponseEntity.ok(adminListDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint sửa thông tin admin dựa trên AdminListDTO
     */
    @PutMapping("/api/admin/{id}")
    @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        try {
            User updatedUser = new User();
            if (requestBody.containsKey("firstName")) {
                updatedUser.setFirstName((String) requestBody.get("firstName"));
            }
            if (requestBody.containsKey("lastName")) {
                updatedUser.setLastName((String) requestBody.get("lastName"));
            }
            if (requestBody.containsKey("address")) {
                Map<String, String> addressMap = (Map<String, String>) requestBody.get("address");
                if (addressMap != null) {
                    Address address = new Address();
                    address.setStreet(addressMap.get("street"));
                    address.setWard(addressMap.get("ward"));
                    address.setDistrict(addressMap.get("district"));
                    address.setCity(addressMap.get("city"));
                    address.setCountry(addressMap.get("country"));
                    updatedUser.setAddress(address);
                }
            }
            if (requestBody.containsKey("username")) {
                updatedUser.setUsername((String) requestBody.get("username"));
            }
            if (requestBody.containsKey("password")) {
                updatedUser.setPassword((String) requestBody.get("password"));
            }
            if (requestBody.containsKey("phone")) {
                updatedUser.setPhone((String) requestBody.get("phone"));
            }
            if (requestBody.containsKey("email")) {
                updatedUser.setEmail((String) requestBody.get("email"));
            }

            User updatedAdmin = userService.updateUser(id, updatedUser);
            if (!"admin".equals(updatedAdmin.getRole())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Người dùng không phải là admin.");
            }
            return ResponseEntity.ok(updatedAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể cập nhật thông tin admin.");
        }
    }

    /**
     * Endpoint thêm admin mới với vai trò tùy ý
     */
    @PostMapping("/api/admin")
    @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, Object> requestBody) {
        try {
            User newUser = new User();
            newUser.setFirstName((String) requestBody.get("firstName"));
            newUser.setLastName((String) requestBody.get("lastName"));
            if (requestBody.containsKey("address")) {
                Map<String, String> addressMap = (Map<String, String>) requestBody.get("address");
                if (addressMap != null) {
                    Address address = new Address();
                    address.setStreet(addressMap.get("street"));
                    address.setWard(addressMap.get("ward"));
                    address.setDistrict(addressMap.get("district"));
                    address.setCity(addressMap.get("city"));
                    address.setCountry(addressMap.get("country"));
                    newUser.setAddress(address);
                }
            }
            newUser.setUsername((String) requestBody.get("username"));
            newUser.setPassword((String) requestBody.get("password"));
            newUser.setPhone((String) requestBody.get("phone"));
            newUser.setEmail((String) requestBody.get("email"));
    
            List<String> adminRoles = (List<String>) requestBody.get("adminRoles");
            if (adminRoles == null || adminRoles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Danh sách vai trò không được để trống.");
            }
    
            User createdAdmin = userService.createAdmin(newUser, adminRoles);
            return ResponseEntity.ok(createdAdmin);
        } catch (InvalidInputException e) {
            try {
                Map<String, String> errors = new ObjectMapper().readValue(e.getMessage(),
                        new TypeReference<Map<String, String>>() {});
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            } catch (Exception jsonError) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể tạo admin mới.");
        }
    }

    @PostMapping("/api/change-password")
    // @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để đổi mật khẩu.");
            }
            if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ không đúng.");
            }
            User updatedUser = new User();
            updatedUser.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(currentUser.getId(), updatedUser);
            return ResponseEntity.ok("Đổi mật khẩu thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể đổi mật khẩu.");
        }
    }
}
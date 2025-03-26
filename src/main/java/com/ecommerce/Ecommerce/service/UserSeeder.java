// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.model.User;
// import com.ecommerce.Ecommerce.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// @Component
// public class UserSeeder implements CommandLineRunner {

//     @Autowired
//     private UserRepository userRepository;

//     private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//     @Override
//     public void run(String... args) throws Exception {
//         // Kiểm tra xem có tài khoản admin chưa
//         if (userRepository.findByUsername("admin123").isEmpty()) {
//             // Tạo tài khoản admin
//             User admin = new User();
//             admin.setUsername("admin123");
//             admin.setPassword(passwordEncoder.encode("123456"));
//             admin.setRole("admin");
//             admin.setEmail("admin123@gmail.com");
//             admin.setFirstName("Admin");
//             admin.setLastName("Admin");
//             admin.setIsActive(true);

//             userRepository.save(admin); // Lưu vào cơ sở dữ liệu
//             System.out.println("Admin account has been seeded.");
//         } else {
//             System.out.println("Admin account already exists.");
//         }
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.AdminRole;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.repository.AdminRoleRepository;
import com.ecommerce.Ecommerce.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Danh sách các admin role và thông tin đăng nhập tương ứng
        String[][] adminData = {
            {"superadmin", "superadmin@example.com", "super_admin"},
            {"productmanager", "productmanager@example.com", "product_manager"},
            {"ordermanager", "ordermanager@example.com", "order_manager"},
            {"marketingmanager", "marketingmanager@example.com", "marketing_manager"},
            {"customersupport", "customersupport@example.com", "customer_support"},
            {"blogmanager", "blogmanager@example.com", "blog_manager"} // Thêm Blog Manager
        };

        // Seeding từng tài khoản admin
        for (String[] data : adminData) {
            String username = data[0];
            String email = data[1];
            String adminRole = data[2];

            System.out.println("Processing adminRole: " + adminRole);

            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                // Tạo tài khoản User nếu chưa tồn tại
                User admin = new User();
                admin.setUsername(username);
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole("admin");
                admin.setEmail(email);
                admin.setFirstName(adminRole.substring(0, 1).toUpperCase() + adminRole.substring(1).replace("_", " "));
                admin.setLastName("Admin");
                admin.setIsActive(true);

                user = userRepository.save(admin);
                System.out.println("Seeded User: " + username + " with email: " + email);
            } else {
                System.out.println("Admin account '" + username + "' already exists.");
            }

            // Kiểm tra và chèn AdminRole nếu chưa tồn tại
            if (adminRoleRepository.findByUserId(user.getId()).isEmpty()) {
                AdminRole role = new AdminRole();
                role.setUserId(user.getId());
                role.setAdminRole(adminRole);
                adminRoleRepository.save(role);
                System.out.println("Seeded AdminRole: " + adminRole + " for userId: " + user.getId());
            } else {
                System.out.println("AdminRole for userId: " + user.getId() + " already exists.");
            }
        }

    }
}
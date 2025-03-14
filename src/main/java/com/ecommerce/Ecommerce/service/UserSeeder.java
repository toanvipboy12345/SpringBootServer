package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem có tài khoản admin chưa
        if (userRepository.findByUsername("admin123").isEmpty()) {
            // Tạo tài khoản admin
            User admin = new User();
            admin.setUsername("admin123");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("admin");
            admin.setEmail("admin123@gmail.com");
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setIsActive(true);

            userRepository.save(admin); // Lưu vào cơ sở dữ liệu
            System.out.println("Admin account has been seeded.");
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}

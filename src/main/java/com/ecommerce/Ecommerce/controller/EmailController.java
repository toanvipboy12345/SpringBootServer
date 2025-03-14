package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/test")
    public String sendTestEmail(@RequestParam String toEmail) {
        try {
            emailService.sendEmail(toEmail, "Kiểm tra gửi email", "Chúc mừng! Email đã được gửi thành công.");
            return "Email đã được gửi!";
        } catch (Exception e) {
            return "Gửi email thất bại: " + e.getMessage();
        }
    }
}
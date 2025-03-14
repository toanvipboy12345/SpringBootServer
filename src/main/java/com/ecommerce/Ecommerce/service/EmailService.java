package com.ecommerce.Ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true để bật HTML

            mailSender.send(message);
            System.out.println("Email HTML đã được gửi thành công tới: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Gửi email HTML thất bại: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email HTML", e);
        }
    }
}
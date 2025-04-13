package com.ecommerce.Ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AboutController {

    private final String ABOUT_FILE_PATH = "src/main/resources/static/about.json"; // Đường dẫn tới file about.json
    private final ObjectMapper objectMapper = new ObjectMapper();

    // API để lấy thông tin từ about.json
    @GetMapping("/about")
    public ResponseEntity<Map<String, Object>> getAboutInfo() {
        try {
            Path filePath = Paths.get(ABOUT_FILE_PATH);
            if (!Files.exists(filePath)) {
                return ResponseEntity.ok(new HashMap<>()); // Trả về rỗng nếu file không tồn tại
            }
            Map<String, Object> aboutData = objectMapper.readValue(filePath.toFile(), Map.class);
            return ResponseEntity.ok(aboutData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to read about info: " + e.getMessage()));
        }
    }

    // API để cập nhật thông tin trong about.json
    @PostMapping("/update/about")
        @RequireAdminRole(roles = {"super_admin"})

    public ResponseEntity<Map<String, Object>> updateAboutInfo(@RequestBody Map<String, Object> updatedData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Path filePath = Paths.get(ABOUT_FILE_PATH);
            // Đảm bảo thư mục tồn tại
            Files.createDirectories(filePath.getParent());
            // Ghi dữ liệu mới vào file
            objectMapper.writeValue(filePath.toFile(), updatedData);
            response.put("success", true);
            response.put("message", "About info updated successfully!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to update about info: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
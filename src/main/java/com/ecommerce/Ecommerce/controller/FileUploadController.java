package com.ecommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // API để upload banner (số lượng động) - Không xóa banner cũ
    @PostMapping("/upload/banners")
            @RequireAdminRole(roles = {"super_admin"})
    public ResponseEntity<Map<String, Object>> uploadBanners(
            @RequestParam("banners") MultipartFile[] banners) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tạo thư mục uploads/ nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lấy danh sách banner hiện có
            List<Path> existingBanners = Files.list(uploadPath)
                    .filter(path -> path.getFileName().toString().startsWith("banner"))
                    .sorted()
                    .collect(Collectors.toList());

            // Tính số thứ tự bắt đầu cho banner mới
            int startIndex = existingBanners.size() + 1;

            // Kiểm tra nếu không có file nào được gửi lên
            if (banners == null || banners.length == 0) {
                response.put("success", false);
                response.put("message", "No banners uploaded!");
                return ResponseEntity.status(400).body(response);
            }

            // Lưu các file mới với tên bannerX.jpeg (tiếp tục từ startIndex)
            int savedCount = 0;
            for (int i = 0; i < banners.length; i++) {
                if (banners[i] != null && !banners[i].isEmpty()) {
                    Path path = uploadPath.resolve("banner" + (startIndex + i) + ".jpeg");
                    Files.write(path, banners[i].getBytes());
                    savedCount++;
                }
            }

            if (savedCount == 0) {
                response.put("success", false);
                response.put("message", "No valid banners were saved!");
                return ResponseEntity.status(400).body(response);
            }

            response.put("success", true);
            response.put("message", "Banners added successfully!");
            response.put("bannerCount", savedCount);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload banners: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // API để upload logo (thay thế logo cũ nếu có)
    @PostMapping("/upload/logo")
    @RequireAdminRole(roles = {"super_admin"})

    public ResponseEntity<Map<String, Object>> uploadLogo(
            @RequestParam("logo") MultipartFile logo) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tạo thư mục uploads/ nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Kiểm tra nếu file rỗng
            if (logo == null || logo.isEmpty()) {
                response.put("success", false);
                response.put("message", "No logo uploaded!");
                return ResponseEntity.status(400).body(response);
            }

            // Tạo tên file cố định cho logo (logo.jpeg)
            String filename = "logo.jpeg";
            Path filePath = uploadPath.resolve(filename);

            // Lưu file logo (ghi đè nếu đã tồn tại)
            Files.write(filePath, logo.getBytes());

            response.put("success", true);
            response.put("message", "Logo uploaded successfully!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload logo: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // API để phục vụ file từ thư mục uploads/
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        // Thêm header để ngăn cache
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // API để lấy danh sách banner hiện có
    @GetMapping("/banners")
    public ResponseEntity<List<String>> getBanners() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return ResponseEntity.ok(List.of());
            }

            List<String> banners = Files.list(uploadPath)
                    .filter(path -> path.getFileName().toString().startsWith("banner"))
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(banners);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // API để xóa một banner cụ thể
    @DeleteMapping("/delete/banner/{filename}")
    @RequireAdminRole(roles = {"super_admin"})

    public ResponseEntity<Map<String, Object>> deleteBanner(@PathVariable String filename) {
        Map<String, Object> response = new HashMap<>();

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);

                // Sau khi xóa, đổi tên các banner còn lại để đảm bảo tên liên tục (banner1, banner2, ...)
                List<Path> remainingBanners = Files.list(Paths.get(uploadDir))
                        .filter(path -> path.getFileName().toString().startsWith("banner"))
                        .sorted()
                        .collect(Collectors.toList());

                // Đổi tên các file còn lại
                for (int i = 0; i < remainingBanners.size(); i++) {
                    Path oldPath = remainingBanners.get(i);
                    Path newPath = Paths.get(uploadDir).resolve("banner" + (i + 1) + ".jpeg");
                    if (!oldPath.equals(newPath)) {
                        Files.move(oldPath, newPath);
                    }
                }

                response.put("success", true);
                response.put("message", "Banner deleted successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Banner not found!");
                return ResponseEntity.status(404).body(response);
            }
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to delete banner: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // API để cập nhật một banner cụ thể
    @PostMapping("/update/banner/{index}")
    @RequireAdminRole(roles = {"super_admin"})

    public ResponseEntity<Map<String, Object>> updateBanner(
            @PathVariable int index,
            @RequestParam("banner") MultipartFile banner) {
        Map<String, Object> response = new HashMap<>();

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Kiểm tra nếu file rỗng
            if (banner == null || banner.isEmpty()) {
                response.put("success", false);
                response.put("message", "No file uploaded!");
                return ResponseEntity.status(400).body(response);
            }

            // Tạo tên file cho banner (banner1.jpeg, banner2.jpeg, ...)
            String filename = "banner" + index + ".jpeg";
            Path filePath = uploadPath.resolve(filename);

            // Lưu file mới
            Files.write(filePath, banner.getBytes());
            response.put("success", true);
            response.put("message", "Banner updated successfully!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to update banner: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
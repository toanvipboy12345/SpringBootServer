package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.dto.BlogDTO;
import com.ecommerce.Ecommerce.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // Thêm bài đăng mới
    @PostMapping
    @RequireAdminRole(roles = {"super_admin", "blog_manager"})

    public ResponseEntity<BlogDTO> createBlog(
            @RequestPart("blog") BlogDTO blogDTO,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) throws IOException {
        // In log bằng System.out.println
        System.out.println("Received POST request to /api/blogs");
        System.out.println("BlogDTO: " + blogDTO);
        if (thumbnailFile != null) {
            System.out.println("ThumbnailFile: name=" + thumbnailFile.getOriginalFilename() + ", size=" + thumbnailFile.getSize());
        } else {
            System.out.println("ThumbnailFile: null");
        }

        BlogDTO createdBlog = blogService.createBlog(blogDTO, thumbnailFile);
        return ResponseEntity.ok(createdBlog);
    }

    // Cập nhật bài đăng
    @PutMapping("/{id}")
        @RequireAdminRole(roles = {"super_admin", "blog_manager"})
    public ResponseEntity<BlogDTO> updateBlog(
            @PathVariable Long id,
            @RequestPart("blog") BlogDTO blogDTO,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) throws IOException {
        BlogDTO updatedBlog = blogService.updateBlog(id, blogDTO, thumbnailFile);
        return ResponseEntity.ok(updatedBlog);
    }

    // Xóa bài đăng
    @DeleteMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "blog_manager"})

    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy bài đăng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable Long id) {
        BlogDTO blogDTO = blogService.getBlogById(id);
        return ResponseEntity.ok(blogDTO);
    }

    // Lấy danh sách tất cả bài đăng
    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        List<BlogDTO> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    // API upload hình ảnh (dùng cho nội dung bài đăng)
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = blogService.uploadImage(file);
        return ResponseEntity.ok(fileUrl);
    }
}
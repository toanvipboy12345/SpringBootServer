package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.Brand;
import com.ecommerce.Ecommerce.service.BrandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý các yêu cầu API liên quan đến thương hiệu (Brand).
 */
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * Lấy danh sách tất cả các thương hiệu.
     */
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    /**
     * Lấy thông tin một thương hiệu dựa trên ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        return brandService.getBrandById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo một thương hiệu mới cùng với hình ảnh (nếu có).
     */
    @PostMapping(value = "/post", consumes = "multipart/form-data")
        @RequireAdminRole(roles = {"super_admin", "product_manager"})

    public ResponseEntity<?> createBrand(
            @RequestPart("brand") String brandJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Brand brand = parseBrandJson(brandJson);
            Brand createdBrand = brandService.createBrand(brand, image);
            return ResponseEntity.status(201).body(createdBrand);
        } catch (IllegalArgumentException e) { // Thay InvalidInputException bằng IllegalArgumentException
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Không thể tải lên hình ảnh: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin một thương hiệu cùng với hình ảnh (nếu có).
     */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})

    public ResponseEntity<?> updateBrand(
            @PathVariable Long id,
            @RequestPart("brand") String brandJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Brand brand = parseBrandJson(brandJson);
            Brand updatedBrand = brandService.updateBrand(id, brand, image);
            return ResponseEntity.ok(updatedBrand);
        } catch (IllegalArgumentException e) { // Thay InvalidInputException bằng IllegalArgumentException
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Không thể cập nhật hình ảnh: " + e.getMessage());
        }
    }

    /**
     * Xóa một thương hiệu dựa trên ID.
     */
    @DeleteMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})

    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) { // Thay InvalidInputException bằng IllegalArgumentException
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tìm kiếm danh sách thương hiệu dựa trên tên (không phân biệt hoa thường).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Brand>> searchBrandsByName(@RequestParam String name) {
        return ResponseEntity.ok(brandService.searchBrandsByName(name));
    }

    // Hàm phụ để parse JSON thành đối tượng Brand
    private Brand parseBrandJson(String brandJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(brandJson, Brand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dữ liệu JSON không hợp lệ: " + e.getMessage());
        }
    }
}
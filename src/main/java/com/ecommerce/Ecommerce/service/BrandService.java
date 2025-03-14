package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Brand;
import com.ecommerce.Ecommerce.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    public Brand createBrand(Brand brand, MultipartFile imageFile) throws IOException {
        validateBrand(brand);
        if (brandRepository.findByName(brand.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại: " + brand.getName());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(brand.getName(), imageFile);
            brand.setImage(imagePath);
        }

        return brandRepository.save(brand);
    }

    public List<Brand> createBrands(List<Brand> brands, List<MultipartFile> imageFiles) throws IOException {
        if (brands.size() != imageFiles.size()) {
            throw new IllegalArgumentException("Số lượng thương hiệu và file hình ảnh phải khớp nhau.");
        }

        for (int i = 0; i < brands.size(); i++) {
            Brand brand = brands.get(i);
            MultipartFile imageFile = imageFiles.get(i);
            validateBrand(brand);
            if (brandRepository.findByName(brand.getName()).isPresent()) {
                throw new IllegalArgumentException("Tên thương hiệu đã tồn tại: " + brand.getName());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = saveImage(brand.getName(), imageFile);
                brand.setImage(imagePath);
            }
        }
        return brandRepository.saveAll(brands);
    }

    public Brand updateBrand(Long id, Brand brand, MultipartFile imageFile) throws IOException {
        validateBrand(brand);
        if (brandRepository.findByName(brand.getName()).isPresent() && 
            !brandRepository.findById(id).map(Brand::getName).orElse("").equals(brand.getName())) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại: " + brand.getName());
        }

        return brandRepository.findById(id).map(existingBrand -> {
            existingBrand.setName(brand.getName());
            existingBrand.setDescription(brand.getDescription());

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String newImagePath = saveImage(brand.getName(), imageFile);
                    if (existingBrand.getImage() != null) {
                        deleteImage(existingBrand.getImage());
                    }
                    existingBrand.setImage(newImagePath);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Không thể cập nhật hình ảnh: " + e.getMessage());
                }
            }

            return brandRepository.save(existingBrand);
        }).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu với id " + id));
    }

    public void deleteBrand(Long id) {
        if (id == null || !brandRepository.existsById(id)) {
            throw new IllegalArgumentException("ID thương hiệu không hợp lệ: " + id);
        }

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thương hiệu với id " + id));
        if (brand.getImage() != null) {
            deleteImage(brand.getImage());
        }

        brandRepository.deleteById(id);
    }

    public List<Brand> searchBrandsByName(String name) {
        return brandRepository.findByNameContainingIgnoreCase(name);
    }

    // Hàm kiểm tra dữ liệu brand
    private void validateBrand(Brand brand) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống hoặc null.");
        }
        if (brand.getDescription() != null && brand.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả không được để trống nếu đã cung cấp.");
        }
    }

    // Hàm lưu hình ảnh với tên thương hiệu
    private String saveImage(String brandName, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        String fileName = brandName.trim().replaceAll("[^a-zA-Z0-9]", "_") + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        int counter = 1;
        while (Files.exists(filePath)) {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileNameWithoutExt + "_" + counter + fileExtension;
            filePath = uploadPath.resolve(fileName);
            counter++;
        }

        Files.copy(imageFile.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    // Hàm xóa hình ảnh
    private void deleteImage(String imagePath) {
        if (imagePath != null) {
            try {
                String relativePath = imagePath.startsWith("/uploads/") ? imagePath.substring(9) : imagePath;
                Path filePath = Paths.get(uploadDir, relativePath);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Không thể xóa hình ảnh: " + e.getMessage());
            }
        }
    }
}
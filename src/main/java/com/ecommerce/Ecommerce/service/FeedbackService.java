package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Feedback;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.repository.FeedbackRepository;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Feedback addFeedback(Long userId, Long productId, Integer rating, String comment, List<MultipartFile> images) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        List<String> imagePaths = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            Path feedbackUploadPath = Paths.get(uploadDir, "feedback");
            for (MultipartFile image : images) {
                String imagePath = saveImage(image);
                imagePaths.add(imagePath);
            }
        }

        Feedback feedback = new Feedback(user, product, rating, comment, imagePaths);
        return feedbackRepository.save(feedback);
    }

    // Cập nhật để dùng findByProduct_Id
    public List<Feedback> getFeedbacksByProductId(Long productId) {
        return feedbackRepository.findByProduct_Id(productId);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir, "feedback");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path filePath = uploadPath.resolve(fileName);

        int counter = 1;
        while (Files.exists(filePath)) {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileNameWithoutExt + "_" + counter + fileExtension;
            filePath = uploadPath.resolve(fileName);
            counter++;
        }

        Files.copy(imageFile.getInputStream(), filePath);
        return "/uploads/feedback/" + fileName;
    }
}
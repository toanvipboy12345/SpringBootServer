package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.Feedback;
import com.ecommerce.Ecommerce.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // Inner static class DTO
    public static class FeedbackResponseDTO {
        private Long id;
        private Long userId;
        private String fullName; // Kết hợp firstName + lastName
        private Long productId;
        private Integer rating;
        private String comment;
        private List<String> images;
        private LocalDateTime createdAt;
        private Double averageRating; // Rating trung bình của sản phẩm

        // Constructor
        public FeedbackResponseDTO(Long id, Long userId, String fullName, Long productId, Integer rating, String comment, List<String> images, LocalDateTime createdAt, Double averageRating) {
            this.id = id;
            this.userId = userId;
            this.fullName = fullName;
            this.productId = productId;
            this.rating = rating;
            this.comment = comment;
            this.images = images;
            this.createdAt = createdAt;
            this.averageRating = averageRating;
        }

        // Getters và Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }

    @PostMapping("/add")
    public ResponseEntity<Feedback> addFeedback(
            @RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Feedback feedback = feedbackService.addFeedback(userId, productId, rating, comment, images);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbacks(@PathVariable Long productId) {
        List<Feedback> feedbacks = feedbackService.getFeedbacksByProductId(productId);

        // Tính rating trung bình
        double averageRating = feedbacks.isEmpty() ? 0.0 :
                feedbacks.stream()
                        .mapToInt(Feedback::getRating)
                        .average()
                        .orElse(0.0);

        // Chuyển đổi sang DTO
        List<FeedbackResponseDTO> feedbackDTOs = feedbacks.stream()
                .map(fb -> new FeedbackResponseDTO(
                        fb.getId(),
                        fb.getUser().getId(),
                        (fb.getUser().getFirstName() != null ? fb.getUser().getFirstName() : "") + " " +
                        (fb.getUser().getLastName() != null ? fb.getUser().getLastName() : ""),
                        fb.getProduct().getId(),
                        fb.getRating(),
                        fb.getComment(),
                        fb.getImages(),
                        fb.getCreatedAt(),
                        averageRating
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(feedbackDTOs);
    }
}
package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Sửa từ findByProductId thành findByProduct_Id
    List<Feedback> findByProduct_Id(Long productId);
}
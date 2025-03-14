package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderId(String orderId);
}

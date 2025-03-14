package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.Transaction;
import com.ecommerce.Ecommerce.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Lấy thông tin giao dịch theo orderId (dành cho FE User/Admin)
    @GetMapping("/{orderId}")
    public ResponseEntity<Transaction> getTransactionByOrderId(@PathVariable String orderId) {
        Transaction transaction = transactionService.getTransactionByOrderId(orderId);
        return ResponseEntity.ok(transaction);
    }

    // Lấy danh sách tất cả giao dịch (dành cho FE Admin)
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}

package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Payment;
import com.ecommerce.Ecommerce.model.PaymentStatus;
import com.ecommerce.Ecommerce.model.Transaction;
import com.ecommerce.Ecommerce.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // @Autowired private PaymentService paymentService; // Loại bỏ dependency này

    // Tạo Transaction sau khi thanh toán thành công (online hoặc COD)
    public Transaction createTransaction(Payment payment, String transId, String payType, String responseCode, String message, String signature) {
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new InvalidInputException("Payment is not successful for orderId: " + payment.getOrderId());
        }

        // Kiểm tra nếu Transaction đã tồn tại
        if (transactionRepository.findByOrderId(payment.getOrderId()).isPresent()) {
            throw new InvalidInputException("Transaction already exists for orderId: " + payment.getOrderId());
        }

        // Tạo Transaction
        Transaction transaction = new Transaction();
        transaction.setOrderId(payment.getOrderId());
        transaction.setPaymentMethod(payment.getPaymentMethod().toString());
        transaction.setAmount(payment.getAmount());
        transaction.setTransId(transId); // Có thể null nếu COD
        transaction.setPayType(payType);
        transaction.setResponseCode(responseCode);
        transaction.setMessage(message);
        transaction.setResponseTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        transaction.setSignature(signature); // Lưu chữ ký từ callback

        return transactionRepository.save(transaction);
    }

    // Lấy Transaction theo orderId
    public Transaction getTransactionByOrderId(String orderId) {
        return transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvalidInputException("Transaction not found for orderId: " + orderId));
    }

    // Lấy danh sách tất cả giao dịch
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}

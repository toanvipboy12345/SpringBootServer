package com.ecommerce.Ecommerce.utils;

import com.ecommerce.Ecommerce.config.VNPayConfig;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VNPayUtil {

    public String createPaymentUrl(String orderId, double amount, String ipAddr, String bankCode) throws Exception {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan don hang " + orderId;
        String vnp_OrderType = "250000";
        String vnp_TxnRef = orderId;
        String vnp_IpAddr = ipAddr;
        String vnp_Locale = "vn";
        String vnp_CurrCode = "VND";
        String vnp_Amount = String.valueOf((long) (amount * 100));

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl); // Sử dụng đường dẫn tương đối
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)).append("&");
                query.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)).append("&");
            }
        }
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        System.out.println("Generated vnp_SecureHash: " + vnp_SecureHash);
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query.toString();
        System.out.println("Payment URL: " + paymentUrl);
        return paymentUrl;
    }

    public boolean validateSignature(Map<String, String> params, String secureHash) throws Exception {
        Map<String, String> vnp_Params = new HashMap<>(params);
        vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)).append("&");
            }
        }
        hashData.setLength(hashData.length() - 1);
        vnp_Params.put("vnp_OrderType", "other"); // Có thể tùy chỉnh
        vnp_Params.put("vnp_Locale", "vn"); // Ngôn ngữ, để hiển thị thông báo hủy
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(secureHash);
    }
}
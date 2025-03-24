package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.dto.RevenueDTO;
import com.ecommerce.Ecommerce.model.dto.SupplierTransactionStatsDTO;
import com.ecommerce.Ecommerce.model.dto.TopProductRevenueDTO;
import com.ecommerce.Ecommerce.model.dto.TopVariantRevenueDTO;
import com.ecommerce.Ecommerce.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/revenue")
    @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager" })
    public ResponseEntity<List<RevenueDTO>> getRevenue(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false, defaultValue = "month") String groupBy) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<RevenueDTO> revenueData = (List<RevenueDTO>) statisticsService.getStatistics(type, startDateTime, endDateTime, groupBy);
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/supplier-transactions")
    @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager" })
    public ResponseEntity<List<SupplierTransactionStatsDTO>> getSupplierTransactionStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<SupplierTransactionStatsDTO> stats = (List<SupplierTransactionStatsDTO>) statisticsService.getStatistics(
            "suppliertransactions", startDateTime, endDateTime, null);
        return ResponseEntity.ok(stats);
    }

    // Endpoint cho Top 10 sản phẩm có doanh thu cao nhất
    @GetMapping("/top-products")
    // @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager" })
    public ResponseEntity<List<TopProductRevenueDTO>> getTopProductsByRevenue(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<TopProductRevenueDTO> topProducts = (List<TopProductRevenueDTO>) statisticsService.getStatistics(
            "topproducts", startDateTime, endDateTime, null);
        return ResponseEntity.ok(topProducts);
    }

    // Endpoint cho Top 10 biến thể sản phẩm có doanh thu cao nhất
    @GetMapping("/top-variants")
    // @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager" })
    public ResponseEntity<List<TopVariantRevenueDTO>> getTopVariantsByRevenue(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<TopVariantRevenueDTO> topVariants = (List<TopVariantRevenueDTO>) statisticsService.getStatistics(
            "topvariants", startDateTime, endDateTime, null);
        return ResponseEntity.ok(topVariants);
    }
}
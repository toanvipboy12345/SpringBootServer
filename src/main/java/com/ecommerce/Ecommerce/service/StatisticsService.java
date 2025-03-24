package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Brand;
import com.ecommerce.Ecommerce.model.OrderStatus;
import com.ecommerce.Ecommerce.model.dto.RevenueDTO;
import com.ecommerce.Ecommerce.model.dto.RevenueDetailDTO;
import com.ecommerce.Ecommerce.model.dto.SupplierTransactionStatsDTO;
import com.ecommerce.Ecommerce.model.dto.TopProductRevenueDTO;
import com.ecommerce.Ecommerce.model.dto.TopVariantRevenueDTO;
import com.ecommerce.Ecommerce.repository.OrderRepository;
import com.ecommerce.Ecommerce.repository.PurchaseOrderRepository;
import com.ecommerce.Ecommerce.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public List<?> getStatistics(String type, LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        switch (type.toLowerCase()) {
            case "time":
                return calculateRevenueByTime(startDate, endDate, groupBy);
            case "brand":
                return calculateRevenueByBrand(startDate, endDate);
            case "paymentmethod":
                return calculateRevenueByPaymentMethod(startDate, endDate);
            case "suppliertransactions":
                return calculateSupplierTransactions(startDate, endDate);
            case "topproducts": // Thêm case cho top sản phẩm
                return calculateTopProductsByRevenue(startDate, endDate);
            case "topvariants": // Thêm case cho top biến thể
                return calculateTopVariantsByRevenue(startDate, endDate);
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    private List<RevenueDTO> calculateRevenueByTime(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        String sqlGroupBy;
        switch (groupBy.toLowerCase()) {
            case "day":
                sqlGroupBy = "DAY";
                break;
            case "week":
                sqlGroupBy = "WEEK";
                break;
            case "month":
                sqlGroupBy = "MONTH";
                break;
            case "year":
                sqlGroupBy = "YEAR";
                break;
            default:
                throw new IllegalArgumentException("Invalid groupBy: " + groupBy);
        }

        List<Object[]> results = orderRepository.findRevenueByTime(sqlGroupBy, startDate, endDate);
        List<RevenueDTO> revenueData = new ArrayList<>();

        for (Object[] result : results) {
            String timeUnit = result[0].toString();
            double total = ((Number) result[1]).doubleValue();
            revenueData.add(new RevenueDTO(timeUnit, total));
        }

        return revenueData;
    }

    private List<RevenueDTO> calculateRevenueByBrand(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.findRevenueByBrand(startDate, endDate);
        Map<Long, Double> revenueByBrand = new HashMap<>();
        Map<Long, String> brandNames = new HashMap<>();
        Map<Long, List<RevenueDetailDTO>> brandDetails = new HashMap<>();

        for (Object[] result : results) {
            Long brandId = ((Number) result[0]).longValue();
            double total = ((Number) result[1]).doubleValue();
            Long productId = ((Number) result[2]).longValue();
            String productName = (String) result[3];
            int quantitySold = ((Number) result[4]).intValue();

            revenueByBrand.merge(brandId, total, Double::sum);

            brandNames.computeIfAbsent(brandId, id -> brandRepository.findById(id)
                    .map(Brand::getName)
                    .orElse("Unknown Brand"));

            brandDetails.computeIfAbsent(brandId, k -> new ArrayList<>())
                    .add(new RevenueDetailDTO(
                            productId.toString(),
                            productName,
                            total,
                            quantitySold
                    ));
        }

        return revenueByBrand.entrySet().stream()
                .map(entry -> new RevenueDTO(
                        brandNames.get(entry.getKey()),
                        entry.getValue(),
                        brandDetails.get(entry.getKey())
                ))
                .collect(Collectors.toList());
    }

    private List<RevenueDTO> calculateRevenueByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.findRevenueByPaymentMethod(startDate, endDate);
        List<RevenueDTO> revenueData = new ArrayList<>();

        for (Object[] result : results) {
            String paymentMethod = result[0].toString();
            double total = ((Number) result[1]).doubleValue();
            revenueData.add(new RevenueDTO(paymentMethod, total));
        }

        return revenueData;
    }

    private List<SupplierTransactionStatsDTO> calculateSupplierTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = purchaseOrderRepository.getTotalTransactionAmountBySupplier(startDate, endDate);
        List<SupplierTransactionStatsDTO> stats = new ArrayList<>();

        for (Object[] result : results) {
            Long supplierId = (Long) result[0];
            String supplierName = (String) result[1];
            Double totalTransactionAmount = (Double) result[2];

            SupplierTransactionStatsDTO dto = new SupplierTransactionStatsDTO(
                supplierId,
                supplierName,
                totalTransactionAmount
            );
            stats.add(dto);
        }

        return stats;
    }

    // Thêm phương thức tính toán Top 10 sản phẩm có doanh thu cao nhất
    private List<TopProductRevenueDTO> calculateTopProductsByRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.findTopProductsByRevenue(startDate, endDate);
        List<TopProductRevenueDTO> topProducts = new ArrayList<>();

        for (Object[] result : results) {
            Long productId = ((Number) result[0]).longValue();
            String productName = (String) result[1];
            Double revenue = ((Number) result[2]).doubleValue();

            topProducts.add(new TopProductRevenueDTO(productId, productName, revenue));
        }

        return topProducts;
    }

    // Thêm phương thức tính toán Top 10 biến thể sản phẩm có doanh thu cao nhất
    private List<TopVariantRevenueDTO> calculateTopVariantsByRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.findTopVariantsByRevenue(startDate, endDate);
        List<TopVariantRevenueDTO> topVariants = new ArrayList<>();

        for (Object[] result : results) {
            Long productId = ((Number) result[0]).longValue();
            Long variantId = ((Number) result[1]).longValue();
            String name = (String) result[2];
            Double price = (Double) result[3];
            Double discountPrice = (Double) result[4];
            String mainImage = (String) result[5];
            Double revenue = ((Number) result[6]).doubleValue();

            topVariants.add(new TopVariantRevenueDTO(productId, variantId, name, price, discountPrice, mainImage, revenue));
        }

        return topVariants;
    }
}
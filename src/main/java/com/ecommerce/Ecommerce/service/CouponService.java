// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.Coupon;
// import com.ecommerce.Ecommerce.repository.CouponRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import com.ecommerce.Ecommerce.model.CouponStatus; // Import enum riêngimport java.time.LocalDateTime;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// public class CouponService {

//     @Autowired
//     private CouponRepository couponRepository;

//     // Validate mã giảm giá
//     public Coupon validateCoupon(String code) {
//         Optional<Coupon> couponOptional = couponRepository.findByCode(code);
//         if (couponOptional.isEmpty()) {
//             throw new InvalidInputException("Invalid coupon code: " + code);
//         }

//         Coupon coupon = couponOptional.get();
//         LocalDateTime now = LocalDateTime.now();

//         if (coupon.getStatus() != CouponStatus.ACTIVE) {
//             throw new InvalidInputException("Coupon is inactive: " + code);
//         }
//         if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
//             throw new InvalidInputException("Coupon is not valid at this time: " + code);
//         }
//         if (coupon.getUsedCount() >= coupon.getMaxUses()) {
//             throw new InvalidInputException("Coupon has reached maximum uses: " + code);
//         }

//         return coupon;
//     }

//     // Thêm mã giảm giá
//     public Coupon createCoupon(Coupon coupon) {
//         if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
//             throw new InvalidInputException("Coupon code already exists: " + coupon.getCode());
//         }
//         coupon.setUsedCount(0); // Khởi tạo số lần sử dụng
//         coupon.setStatus(CouponStatus.ACTIVE); // Mặc định là ACTIVE
//         return couponRepository.save(coupon);
//     }

//     // Cập nhật mã giảm giá
//     public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
//         Coupon coupon = couponRepository.findById(id)
//                 .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));

//         coupon.setCode(updatedCoupon.getCode());
//         coupon.setDiscountRate(updatedCoupon.getDiscountRate());
//         coupon.setStartDate(updatedCoupon.getStartDate());
//         coupon.setEndDate(updatedCoupon.getEndDate());
//         coupon.setMaxUses(updatedCoupon.getMaxUses());
//         coupon.setUsedCount(updatedCoupon.getUsedCount()); // Cập nhật số lần sử dụng
//         coupon.setStatus(updatedCoupon.getStatus());
//         coupon.setApplicableToDiscountedProducts(updatedCoupon.isApplicableToDiscountedProducts());

//         return couponRepository.save(coupon);
//     }

//     // Xóa mã giảm giá (đặt status = INACTIVE)
//     public void deleteCoupon(Long id) {
//         Coupon coupon = couponRepository.findById(id)
//                 .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));
//         coupon.setStatus(CouponStatus.INACTIVE);
//         couponRepository.save(coupon);
//     }

//     // Lấy danh sách mã giảm giá
//     public List<Coupon> getAllCoupons() {
//         return couponRepository.findAll();
//     }
// }
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.Coupon;
// import com.ecommerce.Ecommerce.model.CouponStatus;
// import com.ecommerce.Ecommerce.repository.CouponRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Service
// public class CouponService {

//     @Autowired
//     private CouponRepository couponRepository;

//     // Validate mã giảm giá
//     public Coupon validateCoupon(String code, Long userId) { // Thêm tham số userId
//         Optional<Coupon> couponOptional = couponRepository.findByCode(code);
//         if (couponOptional.isEmpty()) {
//             throw new InvalidInputException("Invalid coupon code: " + code);
//         }

//         Coupon coupon = couponOptional.get();
//         LocalDateTime now = LocalDateTime.now();

//         if (coupon.getStatus() != CouponStatus.ACTIVE) {
//             throw new InvalidInputException("Coupon is inactive: " + code);
//         }
//         if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
//             throw new InvalidInputException("Coupon is not valid at this time: " + code);
//         }
//         if (coupon.getUsedCount() >= coupon.getMaxUses()) {
//             throw new InvalidInputException("Coupon has reached maximum uses: " + code);
//         }

//         // Kiểm tra xem user đã sử dụng mã này chưa
//         if (userId != null && coupon.hasUserUsed(userId)) {
//             throw new InvalidInputException("You have already used this coupon: " + code);
//         }

//         return coupon;
//     }

//     // Thêm mã giảm giá
//     public Coupon createCoupon(Coupon coupon) {
//         if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
//             throw new InvalidInputException("Coupon code already exists: " + coupon.getCode());
//         }
//         coupon.setUsedCount(0); // Khởi tạo số lần sử dụng
//         coupon.setStatus(CouponStatus.ACTIVE); // Mặc định là ACTIVE
//         return couponRepository.save(coupon);
//     }

//     // Cập nhật mã giảm giá
//     public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
//         Coupon coupon = couponRepository.findById(id)
//                 .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));

//         coupon.setCode(updatedCoupon.getCode());
//         coupon.setDiscountRate(updatedCoupon.getDiscountRate());
//         coupon.setStartDate(updatedCoupon.getStartDate());
//         coupon.setEndDate(updatedCoupon.getEndDate());
//         coupon.setMaxUses(updatedCoupon.getMaxUses());
//         coupon.setUsedCount(updatedCoupon.getUsedCount());
//         coupon.setStatus(updatedCoupon.getStatus());
//         coupon.setApplicableToDiscountedProducts(updatedCoupon.isApplicableToDiscountedProducts());
//         coupon.setUsedByUsers(updatedCoupon.getUsedByUsers());

//         return couponRepository.save(coupon);
//     }

//     // Xóa mã giảm giá (đặt status = INACTIVE)
//     public void deleteCoupon(Long id) {
//         Coupon coupon = couponRepository.findById(id)
//                 .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));
//         coupon.setStatus(CouponStatus.INACTIVE);
//         couponRepository.save(coupon);
//     }

//     // Lấy danh sách mã giảm giá
//     public List<Coupon> getAllCoupons() {
//         return couponRepository.findAll();
//     }

//     // Tăng số lần sử dụng của coupon
//     @Transactional
//     public void incrementUsedCount(Coupon coupon, Long userId) {
//         if (coupon.getUsedCount() >= coupon.getMaxUses()) {
//             throw new InvalidInputException("Coupon has reached maximum uses: " + coupon.getCode());
//         }
//         coupon.setUsedCount(coupon.getUsedCount() + 1);
//         if (userId != null) {
//             coupon.addUser(userId); // Thêm userId vào danh sách
//         }
//         couponRepository.save(coupon);
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Coupon;
import com.ecommerce.Ecommerce.model.CouponStatus;
import com.ecommerce.Ecommerce.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    // Validate mã giảm giá
    public Coupon validateCoupon(String code, Long userId) { // Thêm tham số userId
        Optional<Coupon> couponOptional = couponRepository.findByCode(code);
        if (couponOptional.isEmpty()) {
            throw new InvalidInputException("Invalid coupon code: " + code);
        }

        Coupon coupon = couponOptional.get();
        LocalDateTime now = LocalDateTime.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new InvalidInputException("Coupon is inactive: " + code);
        }
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new InvalidInputException("Coupon is not valid at this time: " + code);
        }
        if (coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new InvalidInputException("Coupon has reached maximum uses: " + code);
        }

        // Kiểm tra xem user đã sử dụng mã này chưa
        if (userId != null && coupon.hasUserUsed(userId)) {
            throw new InvalidInputException("You have already used this coupon: " + code);
        }

        return coupon;
    }

    // Thêm mã giảm giá
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new InvalidInputException("Coupon code already exists: " + coupon.getCode());
        }
        coupon.setUsedCount(0); // Khởi tạo số lần sử dụng
        coupon.setStatus(CouponStatus.ACTIVE); // Mặc định là ACTIVE
        return couponRepository.save(coupon);
    }

    // Cập nhật mã giảm giá
    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));

        coupon.setCode(updatedCoupon.getCode());
        coupon.setDiscountRate(updatedCoupon.getDiscountRate());
        coupon.setStartDate(updatedCoupon.getStartDate());
        coupon.setEndDate(updatedCoupon.getEndDate());
        coupon.setMaxUses(updatedCoupon.getMaxUses());
        coupon.setUsedCount(updatedCoupon.getUsedCount());
        coupon.setStatus(updatedCoupon.getStatus());
        coupon.setApplicableToDiscountedProducts(updatedCoupon.isApplicableToDiscountedProducts());
        coupon.setUsedByUsers(updatedCoupon.getUsedByUsers());

        return couponRepository.save(coupon);
    }

    // Xóa mã giảm giá (đặt status = INACTIVE)
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Coupon not found with id: " + id));
        coupon.setStatus(CouponStatus.INACTIVE);
        couponRepository.save(coupon);
    }

    // Lấy danh sách mã giảm giá
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    // Tăng số lần sử dụng của coupon
    @Transactional
    public void incrementUsedCount(Coupon coupon, Long userId) {
        if (coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new InvalidInputException("Coupon has reached maximum uses: " + coupon.getCode());
        }
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        if (userId != null) {
            coupon.addUser(userId); // Thêm userId vào danh sách
        }
        couponRepository.save(coupon);
    }

    // Thêm userId vào danh sách usedByUsers mà không tăng usedCount
    @Transactional
    public void addUserToCoupon(Coupon coupon, Long userId) {
        if (userId != null) {
            coupon.addUser(userId); // Thêm userId vào danh sách
            couponRepository.save(coupon);
        }
    }
}
// package com.ecommerce.Ecommerce.controller;

// import com.ecommerce.Ecommerce.model.Coupon;
// import com.ecommerce.Ecommerce.service.CouponService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/coupons")
// public class CouponController {

//     @Autowired
//     private CouponService couponService;

//     // Validate mã giảm giá
//     @GetMapping("/validate")
//     public ResponseEntity<Coupon> validateCoupon(@RequestParam String code) {
//         Coupon coupon = couponService.validateCoupon(code);
//         return ResponseEntity.ok(coupon);
//     }

//     // Thêm mã giảm giá
//     @PostMapping("/manage")
//     public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
//         Coupon createdCoupon = couponService.createCoupon(coupon);
//         return ResponseEntity.ok(createdCoupon);
//     }

//     // Cập nhật mã giảm giá
//     @PutMapping("/manage/{id}")
//     public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
//         Coupon updatedCoupon = couponService.updateCoupon(id, coupon);
//         return ResponseEntity.ok(updatedCoupon);
//     }

//     // Xóa mã giảm giá
//     @DeleteMapping("/manage/{id}")
//     public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
//         couponService.deleteCoupon(id);
//         return ResponseEntity.ok().build();
//     }

//     // Lấy danh sách mã giảm giá
//     @GetMapping("/manage")
//     public ResponseEntity<List<Coupon>> getAllCoupons() {
//         List<Coupon> coupons = couponService.getAllCoupons();
//         return ResponseEntity.ok(coupons);
//     }
// }
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Coupon;
import com.ecommerce.Ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // Validate mã giảm giá
    @GetMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestParam String code, @RequestParam(required = false) Long userId) {
        try {
            Coupon coupon = couponService.validateCoupon(code, userId);
            return ResponseEntity.ok(coupon);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thêm mã giảm giá
    @PostMapping("/manage")
    @RequireAdminRole(roles = { "super_admin", "marketing_manager" })

    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    // Cập nhật mã giảm giá
    @PutMapping("/manage/{id}")
    @RequireAdminRole(roles = { "super_admin", "marketing_manager" })

    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        Coupon updatedCoupon = couponService.updateCoupon(id, coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    // Xóa mã giảm giá
    @DeleteMapping("/manage/{id}")
    @RequireAdminRole(roles = { "super_admin", "marketing_manager" })

    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok().build();
    }

    // Lấy danh sách mã giảm giá
    @GetMapping("/manage")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}
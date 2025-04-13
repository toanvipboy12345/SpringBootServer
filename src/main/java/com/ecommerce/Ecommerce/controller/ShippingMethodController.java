package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.ShippingMethod;
import com.ecommerce.Ecommerce.service.ShippingMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping-methods")
public class ShippingMethodController {

    @Autowired
    private ShippingMethodService shippingMethodService;

    // Thêm đơn vị vận chuyển
    @PostMapping
            @RequireAdminRole(roles = {"super_admin", "order_manager"})

    public ResponseEntity<ShippingMethod> createShippingMethod(@RequestBody ShippingMethod shippingMethod) {
        ShippingMethod createdShippingMethod = shippingMethodService.createShippingMethod(shippingMethod);
        return ResponseEntity.ok(createdShippingMethod);
    }

    // Cập nhật đơn vị vận chuyển
    @PutMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "order_manager"})

    public ResponseEntity<ShippingMethod> updateShippingMethod(@PathVariable Long id, @RequestBody ShippingMethod shippingMethod) {
        ShippingMethod updatedShippingMethod = shippingMethodService.updateShippingMethod(id, shippingMethod);
        return ResponseEntity.ok(updatedShippingMethod);
    }

    // Xóa đơn vị vận chuyển
    @DeleteMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "order_manager"})

    public ResponseEntity<Map<String, String>> deleteShippingMethod(@PathVariable Long id) {
        try {
            shippingMethodService.deleteShippingMethod(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa đơn vị vận chuyển thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy đơn vị vận chuyển với id: " + id));
        }
    }

    // Lấy danh sách đơn vị vận chuyển
    @GetMapping
    public ResponseEntity<List<ShippingMethod>> getAllShippingMethods() {
        List<ShippingMethod> shippingMethods = shippingMethodService.getAllShippingMethods();
        return ResponseEntity.ok(shippingMethods);
    }

    // Lấy đơn vị vận chuyển theo code
    @GetMapping("/{code}")
    public ResponseEntity<ShippingMethod> getShippingMethodByCode(@PathVariable String code) {
        ShippingMethod shippingMethod = shippingMethodService.getShippingMethodByCode(code);
        return ResponseEntity.ok(shippingMethod);
    }
}
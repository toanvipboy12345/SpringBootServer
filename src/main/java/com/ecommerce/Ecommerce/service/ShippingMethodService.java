package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.ShippingMethod;
import com.ecommerce.Ecommerce.model.ShippingMethodStatus;
import com.ecommerce.Ecommerce.repository.ShippingMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingMethodService {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    // Thêm đơn vị vận chuyển
    public ShippingMethod createShippingMethod(ShippingMethod shippingMethod) {
        if (shippingMethodRepository.findByCode(shippingMethod.getCode()).isPresent()) {
            throw new InvalidInputException("Shipping method code already exists: " + shippingMethod.getCode());
        }
        shippingMethod.setStatus(ShippingMethodStatus.ACTIVE); // Mặc định là ACTIVE
        return shippingMethodRepository.save(shippingMethod);
    }

    // Cập nhật đơn vị vận chuyển
    public ShippingMethod updateShippingMethod(Long id, ShippingMethod updatedShippingMethod) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Shipping method not found with id: " + id));

        shippingMethod.setCode(updatedShippingMethod.getCode());
        shippingMethod.setName(updatedShippingMethod.getName());
        shippingMethod.setShippingFee(updatedShippingMethod.getShippingFee());
        shippingMethod.setStatus(updatedShippingMethod.getStatus());

        return shippingMethodRepository.save(shippingMethod);
    }

    // Xóa đơn vị vận chuyển (đặt status = INACTIVE)
    public void deleteShippingMethod(Long id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Shipping method not found with id: " + id));
        shippingMethod.setStatus(ShippingMethodStatus.INACTIVE);
        shippingMethodRepository.save(shippingMethod);
    }

    // Lấy danh sách đơn vị vận chuyển
    public List<ShippingMethod> getAllShippingMethods() {
        return shippingMethodRepository.findAll();
    }

    // Lấy đơn vị vận chuyển theo code
    public ShippingMethod getShippingMethodByCode(String code) {
        return shippingMethodRepository.findByCode(code)
                .orElseThrow(() -> new InvalidInputException("Shipping method not found with code: " + code));
    }
}
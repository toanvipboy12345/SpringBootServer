package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Supplier;
import com.ecommerce.Ecommerce.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    // Lấy danh sách tất cả nhà cung cấp
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // Lấy nhà cung cấp theo ID
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    // Tạo một nhà cung cấp mới
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
        }
        if (supplier.getCode() == null || supplier.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
        }
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại: " + supplier.getCode());
        }
        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            supplier.setPhone(supplier.getPhone().trim());
        }
        return supplierRepository.save(supplier);
    }

    // Tạo nhiều nhà cung cấp cùng lúc
    @Transactional
    public List<Supplier> createSuppliers(List<Supplier> suppliers) {
        List<Supplier> createdSuppliers = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
            }
            if (supplier.getCode() == null || supplier.getCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
            }
            if (supplierRepository.existsByCode(supplier.getCode())) {
                throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại: " + supplier.getCode());
            }
            if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
                supplier.setPhone(supplier.getPhone().trim());
            }
            createdSuppliers.add(supplier);
        }
        return supplierRepository.saveAll(createdSuppliers);
    }

    // Cập nhật nhà cung cấp
    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại với ID: " + id));

        if (supplier.getName() != null && !supplier.getName().trim().isEmpty()) {
            existingSupplier.setName(supplier.getName());
        }
        if (supplier.getCode() != null && !supplier.getCode().trim().isEmpty()) {
            if (!existingSupplier.getCode().equals(supplier.getCode()) && 
                supplierRepository.existsByCode(supplier.getCode())) {
                throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại: " + supplier.getCode());
            }
            existingSupplier.setCode(supplier.getCode());
        }
        if (supplier.getContact() != null) {
            existingSupplier.setContact(supplier.getContact());
        }
        if (supplier.getAddress() != null) {
            existingSupplier.setAddress(supplier.getAddress());
        }
        if (supplier.getPhone() != null && !supplier.getPhone().trim().isEmpty()) {
            existingSupplier.setPhone(supplier.getPhone().trim());
        }
        return supplierRepository.save(existingSupplier);
    }

    // Xóa nhà cung cấp
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại với ID: " + id));
        supplierRepository.delete(supplier);
    }
}
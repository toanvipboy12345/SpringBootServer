package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
    List<AdminRole> findByUserId(Long userId);
}
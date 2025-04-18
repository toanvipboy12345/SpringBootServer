package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Optional<User> findByGoogle_GoogleId(String googleId);
    long count(); // Đếm tổng số user

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'user'")
    long countByRoleUser(); // Đếm user có role = "user"

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'admin'")
    long countByRoleAdmin(); // Đếm user có role = "admin"
    Optional<User> findByPhone(String phone); // Thêm dòng này
}

package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.dto.StatsDTO;
import com.ecommerce.Ecommerce.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
        @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager", "blog_manager","marketing_manager" })

    public ResponseEntity<StatsDTO> getStats() {
        StatsDTO stats = statsService.getStats();
        return ResponseEntity.ok(stats);
    }
}
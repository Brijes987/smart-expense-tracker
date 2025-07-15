package com.expensetracker.budgetservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/actuator")
public class HealthController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                boolean dbHealthy = connection.isValid(5);
                health.put("database", dbHealthy ? "UP" : "DOWN");
            }
            
            health.put("status", "UP");
            health.put("service", "budget-service");
            health.put("timestamp", LocalDateTime.now());
            health.put("version", "1.0.0");
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("service", "budget-service");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(503).body(health);
        }
    }
}
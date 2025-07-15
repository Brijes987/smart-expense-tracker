package com.expensetracker.budgetservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category", "month", "year"}))
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String category;
    
    @NotNull
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Size(max = 3)
    @Column(length = 3)
    private String currency = "USD";
    
    @NotNull
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(nullable = false)
    private Integer month;
    
    @NotNull
    @Min(value = 2020, message = "Year must be valid")
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "spent_amount", precision = 12, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;
    
    @Column(name = "alert_sent")
    private Boolean alertSent = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Budget() {}
    
    public Budget(Long userId, String category, BigDecimal amount, Integer month, Integer year) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }
    
    // Helper methods
    public BigDecimal getRemainingAmount() {
        return amount.subtract(spentAmount);
    }
    
    public BigDecimal getSpentPercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spentAmount.multiply(BigDecimal.valueOf(100)).divide(amount, 2, BigDecimal.ROUND_HALF_UP);
    }
    
    public boolean isOverBudget() {
        return spentAmount.compareTo(amount) > 0;
    }
    
    public boolean shouldSendAlert(BigDecimal threshold) {
        return !alertSent && getSpentPercentage().compareTo(threshold) >= 0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    
    public Boolean getAlertSent() { return alertSent; }
    public void setAlertSent(Boolean alertSent) { this.alertSent = alertSent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
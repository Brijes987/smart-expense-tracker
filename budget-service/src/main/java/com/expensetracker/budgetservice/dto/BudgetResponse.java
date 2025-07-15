package com.expensetracker.budgetservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response object for budget data")
public class BudgetResponse {
    
    @Schema(description = "Budget ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID", example = "123")
    private Long userId;
    
    @Schema(description = "Budget category", example = "Food")
    private String category;
    
    @Schema(description = "Budget amount", example = "500.00")
    private BigDecimal amount;
    
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    
    @Schema(description = "Budget month (1-12)", example = "3")
    private Integer month;
    
    @Schema(description = "Budget year", example = "2024")
    private Integer year;
    
    @Schema(description = "Amount spent so far", example = "350.75")
    private BigDecimal spentAmount;
    
    @Schema(description = "Remaining budget amount", example = "149.25")
    private BigDecimal remainingAmount;
    
    @Schema(description = "Percentage of budget spent", example = "70.15")
    private BigDecimal spentPercentage;
    
    @Schema(description = "Whether budget is exceeded", example = "false")
    private Boolean isOverBudget;
    
    @Schema(description = "Whether alert has been sent", example = "true")
    private Boolean alertSent;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    // Constructors
    public BudgetResponse() {}
    
    public BudgetResponse(Long id, Long userId, String category, BigDecimal amount, String currency,
                         Integer month, Integer year, BigDecimal spentAmount, BigDecimal remainingAmount,
                         BigDecimal spentPercentage, Boolean isOverBudget, Boolean alertSent,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.month = month;
        this.year = year;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.spentPercentage = spentPercentage;
        this.isOverBudget = isOverBudget;
        this.alertSent = alertSent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    
    public BigDecimal getSpentPercentage() { return spentPercentage; }
    public void setSpentPercentage(BigDecimal spentPercentage) { this.spentPercentage = spentPercentage; }
    
    public Boolean getIsOverBudget() { return isOverBudget; }
    public void setIsOverBudget(Boolean isOverBudget) { this.isOverBudget = isOverBudget; }
    
    public Boolean getAlertSent() { return alertSent; }
    public void setAlertSent(Boolean alertSent) { this.alertSent = alertSent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
package com.expensetracker.expenseservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response object for expense data")
public class ExpenseResponse {
    
    @Schema(description = "Expense ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID", example = "123")
    private Long userId;
    
    @Schema(description = "Expense amount", example = "25.50")
    private BigDecimal amount;
    
    @Schema(description = "Expense category", example = "Food")
    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Expense date", example = "2024-01-15")
    private LocalDate date;
    
    @Schema(description = "Expense description", example = "Lunch at restaurant")
    private String description;
    
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ExpenseResponse() {}
    
    public ExpenseResponse(Long id, Long userId, BigDecimal amount, String category, 
                          LocalDate date, String description, String currency,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
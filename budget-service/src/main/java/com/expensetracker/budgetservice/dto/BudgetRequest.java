package com.expensetracker.budgetservice.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Request object for creating or updating a budget")
public class BudgetRequest {
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Schema(description = "Budget category", example = "Food")
    private String category;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount format is invalid")
    @Schema(description = "Budget amount", example = "500.00")
    private BigDecimal amount;
    
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Schema(description = "Budget month (1-12)", example = "3")
    private Integer month;
    
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be valid")
    @Schema(description = "Budget year", example = "2024")
    private Integer year;
    
    @Size(max = 3, message = "Currency code must be 3 characters")
    @Schema(description = "Currency code", example = "USD")
    private String currency = "USD";
    
    // Constructors
    public BudgetRequest() {}
    
    public BudgetRequest(String category, BigDecimal amount, Integer month, Integer year) {
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }
    
    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
package com.expensetracker.expenseservice.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request object for creating or updating an expense")
public class ExpenseRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount format is invalid")
    @Schema(description = "Expense amount", example = "25.50")
    private BigDecimal amount;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Schema(description = "Expense category", example = "Food")
    private String category;
    
    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Expense date", example = "2024-01-15")
    private LocalDate date;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Expense description", example = "Lunch at restaurant")
    private String description;
    
    @Size(max = 3, message = "Currency code must be 3 characters")
    @Schema(description = "Currency code", example = "USD")
    private String currency = "USD";
    
    // Constructors
    public ExpenseRequest() {}
    
    public ExpenseRequest(BigDecimal amount, String category, LocalDate date, String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }
    
    // Getters and Setters
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
}
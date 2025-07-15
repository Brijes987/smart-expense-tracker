package com.expensetracker.expenseservice.controller;

import com.expensetracker.expenseservice.dto.ExpenseRequest;
import com.expensetracker.expenseservice.dto.ExpenseResponse;
import com.expensetracker.expenseservice.service.ExpenseService;
import com.expensetracker.expenseservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense Management", description = "CRUD operations for expense management")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new expense", description = "Creates a new expense for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Expense created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            ExpenseResponse response = expenseService.createExpense(request, userId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an expense", description = "Updates an existing expense for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpenseResponse> updateExpense(
            @Parameter(description = "Expense ID") @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            ExpenseResponse response = expenseService.updateExpense(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Retrieves a specific expense by ID for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expense retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @Parameter(description = "Expense ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            ExpenseResponse response = expenseService.getExpenseById(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all expenses", description = "Retrieves all expenses for the authenticated user with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        Page<ExpenseResponse> expenses = expenseService.getAllExpenses(userId, page, size);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get expenses by category", description = "Retrieves expenses filtered by category for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategory(
            @Parameter(description = "Expense category") @PathVariable String category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        Page<ExpenseResponse> expenses = expenseService.getExpensesByCategory(userId, category, page, size);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get expenses by date range", description = "Retrieves expenses within a specific date range for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        Page<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate, page, size);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Get expenses by category and date range", description = "Retrieves expenses filtered by both category and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategoryAndDateRange(
            @Parameter(description = "Expense category") @RequestParam String category,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        Page<ExpenseResponse> expenses = expenseService.getExpensesByCategoryAndDateRange(
                userId, category, startDate, endDate, page, size);
        return ResponseEntity.ok(expenses);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense", description = "Deletes a specific expense for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "Expense ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            expenseService.deleteExpense(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get expense summary", description = "Retrieves expense summary including total amounts and categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> getExpenseSummary(Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpenses", expenseService.getTotalExpenses(userId));
        summary.put("categories", expenseService.getCategories(userId));
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/summary/category/{category}")
    @Operation(summary = "Get expense summary by category", description = "Retrieves total expenses for a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category summary retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> getExpenseSummaryByCategory(
            @Parameter(description = "Expense category") @PathVariable String category,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("category", category);
        summary.put("totalAmount", expenseService.getTotalExpensesByCategory(userId, category));
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/summary/date-range")
    @Operation(summary = "Get expense summary by date range", description = "Retrieves total expenses within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Date range summary retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> getExpenseSummaryByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        summary.put("totalAmount", expenseService.getTotalExpensesByDateRange(userId, startDate, endDate));
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Health check endpoint for the expense service")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Expense Service is running!");
    }
}
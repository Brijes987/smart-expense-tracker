package com.expensetracker.budgetservice.controller;

import com.expensetracker.budgetservice.dto.BudgetRequest;
import com.expensetracker.budgetservice.dto.BudgetResponse;
import com.expensetracker.budgetservice.service.BudgetService;
import com.expensetracker.budgetservice.service.BudgetSchedulerService;
import com.expensetracker.budgetservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budget Management", description = "CRUD operations for budget management and alerts")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private BudgetSchedulerService budgetSchedulerService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new budget", description = "Creates a new monthly budget for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Budget created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or budget already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            BudgetResponse response = budgetService.createBudget(request, userId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a budget", description = "Updates an existing budget for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public ResponseEntity<BudgetResponse> updateBudget(
            @Parameter(description = "Budget ID") @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            BudgetResponse response = budgetService.updateBudget(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieves a specific budget by ID for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budget retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public ResponseEntity<BudgetResponse> getBudgetById(
            @Parameter(description = "Budget ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            BudgetResponse response = budgetService.getBudgetById(id, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieves all budgets for the authenticated user with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<BudgetResponse>> getAllBudgets(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        Page<BudgetResponse> budgets = budgetService.getAllBudgets(userId, page, size);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/year/{year}")
    @Operation(summary = "Get budgets by year", description = "Retrieves all budgets for a specific year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BudgetResponse>> getBudgetsByYear(
            @Parameter(description = "Year") @PathVariable Integer year,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        List<BudgetResponse> budgets = budgetService.getBudgetsByYear(userId, year);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/year/{year}/month/{month}")
    @Operation(summary = "Get budgets by month", description = "Retrieves all budgets for a specific month and year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BudgetResponse>> getBudgetsByMonth(
            @Parameter(description = "Year") @PathVariable Integer year,
            @Parameter(description = "Month (1-12)") @PathVariable Integer month,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        List<BudgetResponse> budgets = budgetService.getBudgetsByMonth(userId, year, month);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get budgets by category", description = "Retrieves all budgets for a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BudgetResponse>> getBudgetsByCategory(
            @Parameter(description = "Budget category") @PathVariable String category,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        List<BudgetResponse> budgets = budgetService.getBudgetsByCategory(userId, category);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/current/{category}")
    @Operation(summary = "Get current month budget", description = "Retrieves budget for current month and specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budget retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BudgetResponse> getCurrentMonthBudget(
            @Parameter(description = "Budget category") @PathVariable String category,
            Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        BudgetResponse budget = budgetService.getCurrentMonthBudget(userId, category);
        
        if (budget != null) {
            return ResponseEntity.ok(budget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/over-budget")
    @Operation(summary = "Get over-budget items", description = "Retrieves all budgets that have been exceeded")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Over-budget items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BudgetResponse>> getOverBudgets(Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        List<BudgetResponse> overBudgets = budgetService.getOverBudgets(userId);
        return ResponseEntity.ok(overBudgets);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget", description = "Deletes a specific budget for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public ResponseEntity<Void> deleteBudget(
            @Parameter(description = "Budget ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = userService.getUserIdFromAuthentication(authentication);
            budgetService.deleteBudget(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/refresh")
    @Operation(summary = "Refresh budget spending", description = "Updates budget spending amount from expense service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Budget refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public ResponseEntity<BudgetResponse> refreshBudgetSpending(
            @Parameter(description = "Budget ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            String authToken = "Bearer " + authentication.getCredentials();
            BudgetResponse response = budgetService.updateBudgetSpending(id, authToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get budget summary", description = "Retrieves budget summary including categories and years")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> getBudgetSummary(Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("categories", budgetService.getCategories(userId));
        summary.put("years", budgetService.getYears(userId));
        summary.put("overBudgetCount", budgetService.getOverBudgets(userId).size());
        
        return ResponseEntity.ok(summary);
    }
    
    // Admin/Testing endpoints
    @PostMapping("/admin/trigger-alerts")
    @Operation(summary = "Trigger budget alerts", description = "Manually trigger budget alert check (Admin/Testing)")
    public ResponseEntity<String> triggerBudgetAlerts() {
        budgetSchedulerService.triggerBudgetAlertCheck();
        return ResponseEntity.ok("Budget alert check triggered successfully");
    }
    
    @PostMapping("/admin/trigger-weekly-summary")
    @Operation(summary = "Trigger weekly summary", description = "Manually trigger weekly summary (Admin/Testing)")
    public ResponseEntity<String> triggerWeeklySummary() {
        budgetSchedulerService.triggerWeeklySummary();
        return ResponseEntity.ok("Weekly summary triggered successfully");
    }
    
    @PostMapping("/admin/trigger-monthly-report")
    @Operation(summary = "Trigger monthly report", description = "Manually trigger monthly report (Admin/Testing)")
    public ResponseEntity<String> triggerMonthlyReport() {
        budgetSchedulerService.triggerMonthlyReport();
        return ResponseEntity.ok("Monthly report triggered successfully");
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Health check endpoint for the budget service")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Budget Service is running!");
    }
}
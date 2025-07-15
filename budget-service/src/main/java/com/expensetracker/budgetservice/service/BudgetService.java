package com.expensetracker.budgetservice.service;

import com.expensetracker.budgetservice.dto.BudgetRequest;
import com.expensetracker.budgetservice.dto.BudgetResponse;
import com.expensetracker.budgetservice.entity.Budget;
import com.expensetracker.budgetservice.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private ExpenseServiceClient expenseServiceClient;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${budget.alert.threshold:80}")
    private BigDecimal alertThreshold;
    
    public BudgetResponse createBudget(BudgetRequest request, Long userId) {
        // Check if budget already exists for this user, category, month, year
        if (budgetRepository.existsByUserIdAndCategoryAndMonthAndYear(
                userId, request.getCategory(), request.getMonth(), request.getYear())) {
            throw new RuntimeException("Budget already exists for this category and period");
        }
        
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCurrency(request.getCurrency());
        
        Budget savedBudget = budgetRepository.save(budget);
        return mapToResponse(savedBudget);
    }
    
    public BudgetResponse updateBudget(Long budgetId, BudgetRequest request, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found or access denied"));
        
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCurrency(request.getCurrency());
        
        Budget updatedBudget = budgetRepository.save(budget);
        return mapToResponse(updatedBudget);
    }
    
    public BudgetResponse getBudgetById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found or access denied"));
        return mapToResponse(budget);
    }
    
    public Page<BudgetResponse> getAllBudgets(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Budget> budgets = budgetRepository.findByUserIdOrderByYearDescMonthDesc(userId, pageable);
        return budgets.map(this::mapToResponse);
    }
    
    public List<BudgetResponse> getBudgetsByYear(Long userId, Integer year) {
        List<Budget> budgets = budgetRepository.findByUserIdAndYearOrderByMonthAsc(userId, year);
        return budgets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<BudgetResponse> getBudgetsByMonth(Long userId, Integer year, Integer month) {
        List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);
        return budgets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<BudgetResponse> getBudgetsByCategory(Long userId, String category) {
        List<Budget> budgets = budgetRepository.findByUserIdAndCategoryOrderByYearDescMonthDesc(userId, category);
        return budgets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found or access denied"));
        budgetRepository.delete(budget);
    }
    
    public BudgetResponse updateBudgetSpending(Long budgetId, String authToken) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        // Fetch actual spending from expense service
        BigDecimal actualSpending = expenseServiceClient.getTotalExpensesByUserAndCategoryAndMonth(
                authToken, budget.getUserId(), budget.getCategory(), budget.getMonth(), budget.getYear());
        
        budget.setSpentAmount(actualSpending);
        
        // Check if alert should be sent
        if (budget.shouldSendAlert(alertThreshold)) {
            notificationService.sendBudgetAlert(budget);
            budget.setAlertSent(true);
        }
        
        Budget updatedBudget = budgetRepository.save(budget);
        return mapToResponse(updatedBudget);
    }
    
    public void refreshAllBudgetSpending(String authToken) {
        List<Budget> allBudgets = budgetRepository.findAll();
        
        for (Budget budget : allBudgets) {
            BigDecimal actualSpending = expenseServiceClient.getTotalExpensesByUserAndCategoryAndMonth(
                    authToken, budget.getUserId(), budget.getCategory(), budget.getMonth(), budget.getYear());
            
            budget.setSpentAmount(actualSpending);
            
            // Check if alert should be sent
            if (budget.shouldSendAlert(alertThreshold)) {
                notificationService.sendBudgetAlert(budget);
                budget.setAlertSent(true);
            }
        }
        
        budgetRepository.saveAll(allBudgets);
    }
    
    public List<BudgetResponse> getOverBudgets(Long userId) {
        List<Budget> overBudgets = budgetRepository.findOverBudgetsByUserId(userId);
        return overBudgets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public BudgetResponse getCurrentMonthBudget(Long userId, String category) {
        LocalDate now = LocalDate.now();
        return budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, category, now.getMonthValue(), now.getYear())
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    public List<String> getCategories(Long userId) {
        return budgetRepository.findDistinctCategoriesByUserId(userId);
    }
    
    public List<Integer> getYears(Long userId) {
        return budgetRepository.findDistinctYearsByUserId(userId);
    }
    
    private BudgetResponse mapToResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getUserId(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getCurrency(),
                budget.getMonth(),
                budget.getYear(),
                budget.getSpentAmount(),
                budget.getRemainingAmount(),
                budget.getSpentPercentage(),
                budget.isOverBudget(),
                budget.getAlertSent(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
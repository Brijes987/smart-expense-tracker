package com.expensetracker.budgetservice.service;

import com.expensetracker.budgetservice.dto.BudgetRequest;
import com.expensetracker.budgetservice.dto.BudgetResponse;
import com.expensetracker.budgetservice.entity.Budget;
import com.expensetracker.budgetservice.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    
    @Mock
    private BudgetRepository budgetRepository;
    
    @Mock
    private ExpenseServiceClient expenseServiceClient;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private BudgetService budgetService;
    
    private BudgetRequest budgetRequest;
    private Budget budget;
    private Long userId;
    
    @BeforeEach
    void setUp() {
        userId = 1L;
        
        budgetRequest = new BudgetRequest();
        budgetRequest.setCategory("Food");
        budgetRequest.setAmount(new BigDecimal("500.00"));
        budgetRequest.setMonth(1);
        budgetRequest.setYear(2024);
        budgetRequest.setCurrency("USD");
        
        budget = new Budget();
        budget.setId(1L);
        budget.setUserId(userId);
        budget.setCategory("Food");
        budget.setAmount(new BigDecimal("500.00"));
        budget.setMonth(1);
        budget.setYear(2024);
        budget.setCurrency("USD");
        budget.setSpentAmount(BigDecimal.ZERO);
        budget.setAlertSent(false);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void createBudget_ShouldReturnBudgetResponse() {
        // Given
        when(budgetRepository.existsByUserIdAndCategoryAndMonthAndYear(userId, "Food", 1, 2024))
                .thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        
        // When
        BudgetResponse result = budgetService.createBudget(budgetRequest, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(budget.getId(), result.getId());
        assertEquals(budget.getUserId(), result.getUserId());
        assertEquals(budget.getCategory(), result.getCategory());
        assertEquals(budget.getAmount(), result.getAmount());
        assertEquals(budget.getMonth(), result.getMonth());
        assertEquals(budget.getYear(), result.getYear());
        
        verify(budgetRepository, times(1)).existsByUserIdAndCategoryAndMonthAndYear(userId, "Food", 1, 2024);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }
    
    @Test
    void createBudget_ShouldThrowException_WhenBudgetAlreadyExists() {
        // Given
        when(budgetRepository.existsByUserIdAndCategoryAndMonthAndYear(userId, "Food", 1, 2024))
                .thenReturn(true);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            budgetService.createBudget(budgetRequest, userId);
        });
        
        verify(budgetRepository, times(1)).existsByUserIdAndCategoryAndMonthAndYear(userId, "Food", 1, 2024);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
    
    @Test
    void updateBudget_ShouldReturnUpdatedBudgetResponse() {
        // Given
        when(budgetRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        
        // When
        BudgetResponse result = budgetService.updateBudget(1L, budgetRequest, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(budget.getId(), result.getId());
        verify(budgetRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(budgetRepository, times(1)).save(budget);
    }
    
    @Test
    void updateBudget_ShouldThrowException_WhenBudgetNotFound() {
        // Given
        when(budgetRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            budgetService.updateBudget(1L, budgetRequest, userId);
        });
        
        verify(budgetRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(budgetRepository, never()).save(any(Budget.class));
    }
    
    @Test
    void getBudgetById_ShouldReturnBudgetResponse() {
        // Given
        when(budgetRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(budget));
        
        // When
        BudgetResponse result = budgetService.getBudgetById(1L, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(budget.getId(), result.getId());
        verify(budgetRepository, times(1)).findByIdAndUserId(1L, userId);
    }
    
    @Test
    void getAllBudgets_ShouldReturnPageOfBudgets() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Budget> budgetPage = new PageImpl<>(Arrays.asList(budget));
        when(budgetRepository.findByUserIdOrderByYearDescMonthDesc(userId, pageable)).thenReturn(budgetPage);
        
        // When
        Page<BudgetResponse> result = budgetService.getAllBudgets(userId, 0, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(budget.getId(), result.getContent().get(0).getId());
        verify(budgetRepository, times(1)).findByUserIdOrderByYearDescMonthDesc(userId, pageable);
    }
    
    @Test
    void deleteBudget_ShouldDeleteBudget() {
        // Given
        when(budgetRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(budget));
        
        // When
        budgetService.deleteBudget(1L, userId);
        
        // Then
        verify(budgetRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(budgetRepository, times(1)).delete(budget);
    }
    
    @Test
    void updateBudgetSpending_ShouldUpdateSpentAmountAndSendAlert() {
        // Given
        BigDecimal spentAmount = new BigDecimal("400.00"); // 80% of 500
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
        when(expenseServiceClient.getTotalExpensesByUserAndCategoryAndMonth(
                any(), eq(userId), eq("Food"), eq(1), eq(2024)))
                .thenReturn(spentAmount);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        
        // When
        BudgetResponse result = budgetService.updateBudgetSpending(1L, "Bearer token");
        
        // Then
        assertNotNull(result);
        verify(budgetRepository, times(1)).findById(1L);
        verify(expenseServiceClient, times(1)).getTotalExpensesByUserAndCategoryAndMonth(
                any(), eq(userId), eq("Food"), eq(1), eq(2024));
        verify(notificationService, times(1)).sendBudgetAlert(budget);
        verify(budgetRepository, times(1)).save(budget);
    }
}
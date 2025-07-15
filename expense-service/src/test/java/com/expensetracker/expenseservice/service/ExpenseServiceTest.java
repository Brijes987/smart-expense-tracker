package com.expensetracker.expenseservice.service;

import com.expensetracker.expenseservice.dto.ExpenseRequest;
import com.expensetracker.expenseservice.dto.ExpenseResponse;
import com.expensetracker.expenseservice.entity.Expense;
import com.expensetracker.expenseservice.repository.ExpenseRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    
    @Mock
    private ExpenseRepository expenseRepository;
    
    @InjectMocks
    private ExpenseService expenseService;
    
    private ExpenseRequest expenseRequest;
    private Expense expense;
    private Long userId;
    
    @BeforeEach
    void setUp() {
        userId = 1L;
        
        expenseRequest = new ExpenseRequest();
        expenseRequest.setAmount(new BigDecimal("25.50"));
        expenseRequest.setCategory("Food");
        expenseRequest.setDate(LocalDate.now());
        expenseRequest.setDescription("Lunch at restaurant");
        expenseRequest.setCurrency("USD");
        
        expense = new Expense();
        expense.setId(1L);
        expense.setUserId(userId);
        expense.setAmount(new BigDecimal("25.50"));
        expense.setCategory("Food");
        expense.setDate(LocalDate.now());
        expense.setDescription("Lunch at restaurant");
        expense.setCurrency("USD");
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void createExpense_ShouldReturnExpenseResponse() {
        // Given
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        
        // When
        ExpenseResponse result = expenseService.createExpense(expenseRequest, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(expense.getId(), result.getId());
        assertEquals(expense.getUserId(), result.getUserId());
        assertEquals(expense.getAmount(), result.getAmount());
        assertEquals(expense.getCategory(), result.getCategory());
        assertEquals(expense.getDate(), result.getDate());
        assertEquals(expense.getDescription(), result.getDescription());
        
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
    
    @Test
    void updateExpense_ShouldReturnUpdatedExpenseResponse() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        
        // When
        ExpenseResponse result = expenseService.updateExpense(1L, expenseRequest, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(expense.getId(), result.getId());
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(expenseRepository, times(1)).save(expense);
    }
    
    @Test
    void updateExpense_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(1L, expenseRequest, userId);
        });
        
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(expenseRepository, never()).save(any(Expense.class));
    }
    
    @Test
    void getExpenseById_ShouldReturnExpenseResponse() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(expense));
        
        // When
        ExpenseResponse result = expenseService.getExpenseById(1L, userId);
        
        // Then
        assertNotNull(result);
        assertEquals(expense.getId(), result.getId());
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
    }
    
    @Test
    void getExpenseById_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            expenseService.getExpenseById(1L, userId);
        });
        
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
    }
    
    @Test
    void getAllExpenses_ShouldReturnPageOfExpenses() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expense> expensePage = new PageImpl<>(Arrays.asList(expense));
        when(expenseRepository.findByUserIdOrderByDateDesc(userId, pageable)).thenReturn(expensePage);
        
        // When
        Page<ExpenseResponse> result = expenseService.getAllExpenses(userId, 0, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(expense.getId(), result.getContent().get(0).getId());
        verify(expenseRepository, times(1)).findByUserIdOrderByDateDesc(userId, pageable);
    }
    
    @Test
    void deleteExpense_ShouldDeleteExpense() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(expense));
        
        // When
        expenseService.deleteExpense(1L, userId);
        
        // Then
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(expenseRepository, times(1)).delete(expense);
    }
    
    @Test
    void deleteExpense_ShouldThrowException_WhenExpenseNotFound() {
        // Given
        when(expenseRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L, userId);
        });
        
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, userId);
        verify(expenseRepository, never()).delete(any(Expense.class));
    }
    
    @Test
    void getTotalExpenses_ShouldReturnTotalAmount() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("100.00");
        when(expenseRepository.getTotalExpensesByUserId(userId)).thenReturn(expectedTotal);
        
        // When
        BigDecimal result = expenseService.getTotalExpenses(userId);
        
        // Then
        assertEquals(expectedTotal, result);
        verify(expenseRepository, times(1)).getTotalExpensesByUserId(userId);
    }
    
    @Test
    void getTotalExpenses_ShouldReturnZero_WhenNoExpenses() {
        // Given
        when(expenseRepository.getTotalExpensesByUserId(userId)).thenReturn(null);
        
        // When
        BigDecimal result = expenseService.getTotalExpenses(userId);
        
        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(expenseRepository, times(1)).getTotalExpensesByUserId(userId);
    }
}
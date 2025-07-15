package com.expensetracker.expenseservice.service;

import com.expensetracker.expenseservice.dto.ExpenseRequest;
import com.expensetracker.expenseservice.dto.ExpenseResponse;
import com.expensetracker.expenseservice.entity.Expense;
import com.expensetracker.expenseservice.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public ExpenseResponse createExpense(ExpenseRequest request, Long userId) {
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setCurrency(request.getCurrency());
        
        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }
    
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, Long userId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));
        
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setCurrency(request.getCurrency());
        
        Expense updatedExpense = expenseRepository.save(expense);
        return mapToResponse(updatedExpense);
    }
    
    public ExpenseResponse getExpenseById(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));
        return mapToResponse(expense);
    }
    
    public Page<ExpenseResponse> getAllExpenses(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(userId, pageable);
        return expenses.map(this::mapToResponse);
    }
    
    public Page<ExpenseResponse> getExpensesByCategory(Long userId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseRepository.findByUserIdAndCategoryOrderByDateDesc(userId, category, pageable);
        return expenses.map(this::mapToResponse);
    }
    
    public Page<ExpenseResponse> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate, pageable);
        return expenses.map(this::mapToResponse);
    }
    
    public Page<ExpenseResponse> getExpensesByCategoryAndDateRange(Long userId, String category, 
                                                                 LocalDate startDate, LocalDate endDate, 
                                                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseRepository.findByUserIdAndCategoryAndDateBetweenOrderByDateDesc(
                userId, category, startDate, endDate, pageable);
        return expenses.map(this::mapToResponse);
    }
    
    public void deleteExpense(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));
        expenseRepository.delete(expense);
    }
    
    public BigDecimal getTotalExpenses(Long userId) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalExpensesByCategory(Long userId, String category) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserIdAndCategory(userId, category);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<String> getCategories(Long userId) {
        return expenseRepository.findDistinctCategoriesByUserId(userId);
    }
    
    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getUserId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate(),
                expense.getDescription(),
                expense.getCurrency(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
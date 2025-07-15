package com.expensetracker.expenseservice.repository;

import com.expensetracker.expenseservice.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Find all expenses for a specific user
    Page<Expense> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
    
    // Find expense by ID and user ID (for security)
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
    
    // Find expenses by user and category
    Page<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category, Pageable pageable);
    
    // Find expenses by user and date range
    Page<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Find expenses by user, category and date range
    Page<Expense> findByUserIdAndCategoryAndDateBetweenOrderByDateDesc(
            Long userId, String category, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Get total expenses for a user
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId")
    BigDecimal getTotalExpensesByUserId(@Param("userId") Long userId);
    
    // Get total expenses for a user by category
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND e.category = :category")
    BigDecimal getTotalExpensesByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    
    // Get total expenses for a user in date range
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByUserIdAndDateRange(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    // Get distinct categories for a user
    @Query("SELECT DISTINCT e.category FROM Expense e WHERE e.userId = :userId ORDER BY e.category")
    List<String> findDistinctCategoriesByUserId(@Param("userId") Long userId);
    
    // Delete all expenses for a user
    void deleteByUserId(Long userId);
}
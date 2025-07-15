package com.expensetracker.budgetservice.repository;

import com.expensetracker.budgetservice.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    // Find all budgets for a specific user
    Page<Budget> findByUserIdOrderByYearDescMonthDesc(Long userId, Pageable pageable);
    
    // Find budget by ID and user ID (for security)
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    
    // Find budget by user, category, month, and year
    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(Long userId, String category, Integer month, Integer year);
    
    // Find budgets by user and year
    List<Budget> findByUserIdAndYearOrderByMonthAsc(Long userId, Integer year);
    
    // Find budgets by user, year, and month
    List<Budget> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
    
    // Find budgets by user and category
    List<Budget> findByUserIdAndCategoryOrderByYearDescMonthDesc(Long userId, String category);
    
    // Find budgets that need alerts (spent >= threshold and alert not sent)
    @Query("SELECT b FROM Budget b WHERE b.alertSent = false AND " +
           "(b.spentAmount * 100 / b.amount) >= :threshold")
    List<Budget> findBudgetsNeedingAlert(@Param("threshold") BigDecimal threshold);
    
    // Find over-budget budgets for a user
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.spentAmount > b.amount")
    List<Budget> findOverBudgetsByUserId(@Param("userId") Long userId);
    
    // Get total budget amount for a user in a specific month/year
    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.userId = :userId AND b.month = :month AND b.year = :year")
    BigDecimal getTotalBudgetByUserAndMonth(@Param("userId") Long userId, @Param("month") Integer month, @Param("year") Integer year);
    
    // Get total spent amount for a user in a specific month/year
    @Query("SELECT SUM(b.spentAmount) FROM Budget b WHERE b.userId = :userId AND b.month = :month AND b.year = :year")
    BigDecimal getTotalSpentByUserAndMonth(@Param("userId") Long userId, @Param("month") Integer month, @Param("year") Integer year);
    
    // Get distinct categories for a user
    @Query("SELECT DISTINCT b.category FROM Budget b WHERE b.userId = :userId ORDER BY b.category")
    List<String> findDistinctCategoriesByUserId(@Param("userId") Long userId);
    
    // Get distinct years for a user
    @Query("SELECT DISTINCT b.year FROM Budget b WHERE b.userId = :userId ORDER BY b.year DESC")
    List<Integer> findDistinctYearsByUserId(@Param("userId") Long userId);
    
    // Delete all budgets for a user
    void deleteByUserId(Long userId);
    
    // Check if budget exists for user, category, month, year
    boolean existsByUserIdAndCategoryAndMonthAndYear(Long userId, String category, Integer month, Integer year);
}
package com.expensetracker.budgetservice.service;

import com.expensetracker.budgetservice.entity.Budget;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {
    
    /**
     * Send budget alert notification
     * In a real implementation, this would:
     * - Send email via SMTP
     * - Send push notification
     * - Send SMS
     * - Log to external monitoring system
     * 
     * For demo purposes, we'll simulate with console logging
     */
    public void sendBudgetAlert(Budget budget) {
        String alertMessage = createAlertMessage(budget);
        
        // Simulate email notification
        simulateEmailNotification(budget, alertMessage);
        
        // Log the alert
        System.out.println("BUDGET ALERT SENT: " + alertMessage);
    }
    
    private String createAlertMessage(Budget budget) {
        String monthName = getMonthName(budget.getMonth());
        
        if (budget.isOverBudget()) {
            return String.format(
                "🚨 BUDGET EXCEEDED! Your %s budget for %s %d has been exceeded. " +
                "Budget: %s %.2f, Spent: %.2f (%.1f%% over budget)",
                budget.getCategory(),
                monthName,
                budget.getYear(),
                budget.getCurrency(),
                budget.getAmount(),
                budget.getSpentAmount(),
                budget.getSpentPercentage().subtract(java.math.BigDecimal.valueOf(100))
            );
        } else {
            return String.format(
                "⚠️ BUDGET ALERT! You've reached %.1f%% of your %s budget for %s %d. " +
                "Budget: %s %.2f, Spent: %.2f, Remaining: %.2f",
                budget.getSpentPercentage(),
                budget.getCategory(),
                monthName,
                budget.getYear(),
                budget.getCurrency(),
                budget.getAmount(),
                budget.getSpentAmount(),
                budget.getRemainingAmount()
            );
        }
    }
    
    private void simulateEmailNotification(Budget budget, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        System.out.println("=== EMAIL NOTIFICATION SENT ===");
        System.out.println("To: user" + budget.getUserId() + "@example.com");
        System.out.println("Subject: Budget Alert - " + budget.getCategory() + " Category");
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Message: " + message);
        System.out.println("===============================");
    }
    
    private String getMonthName(int month) {
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return months[month - 1];
    }
    
    /**
     * Send weekly budget summary
     */
    public void sendWeeklyBudgetSummary(Long userId, String summary) {
        System.out.println("=== WEEKLY BUDGET SUMMARY ===");
        System.out.println("To: user" + userId + "@example.com");
        System.out.println("Subject: Weekly Budget Summary");
        System.out.println("Message: " + summary);
        System.out.println("=============================");
    }
    
    /**
     * Send monthly budget report
     */
    public void sendMonthlyBudgetReport(Long userId, String report) {
        System.out.println("=== MONTHLY BUDGET REPORT ===");
        System.out.println("To: user" + userId + "@example.com");
        System.out.println("Subject: Monthly Budget Report");
        System.out.println("Message: " + report);
        System.out.println("=============================");
    }
}
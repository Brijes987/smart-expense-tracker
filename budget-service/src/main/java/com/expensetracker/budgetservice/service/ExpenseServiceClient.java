package com.expensetracker.budgetservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ExpenseServiceClient {
    
    @Value("${services.expense-service.url}")
    private String expenseServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public ExpenseServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Get total expenses for a user in a specific month/year and category
     */
    public BigDecimal getTotalExpensesByUserAndCategoryAndMonth(String authToken, Long userId, 
                                                               String category, Integer month, Integer year) {
        try {
            String startDate = String.format("%d-%02d-01", year, month);
            String endDate = String.format("%d-%02d-%02d", year, month, getLastDayOfMonth(month, year));
            
            String url = String.format("%s/api/expenses/summary/date-range?startDate=%s&endDate=%s", 
                                     expenseServiceUrl, startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("totalAmount")) {
                Object totalAmount = response.getBody().get("totalAmount");
                if (totalAmount != null) {
                    return new BigDecimal(totalAmount.toString());
                }
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error fetching expenses from expense service: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get total expenses for a user in a specific month/year
     */
    public BigDecimal getTotalExpensesByUserAndMonth(String authToken, Long userId, Integer month, Integer year) {
        try {
            String startDate = String.format("%d-%02d-01", year, month);
            String endDate = String.format("%d-%02d-%02d", year, month, getLastDayOfMonth(month, year));
            
            String url = String.format("%s/api/expenses/summary/date-range?startDate=%s&endDate=%s", 
                                     expenseServiceUrl, startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("totalAmount")) {
                Object totalAmount = response.getBody().get("totalAmount");
                if (totalAmount != null) {
                    return new BigDecimal(totalAmount.toString());
                }
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error fetching expenses from expense service: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    private int getLastDayOfMonth(int month, int year) {
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }
    
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
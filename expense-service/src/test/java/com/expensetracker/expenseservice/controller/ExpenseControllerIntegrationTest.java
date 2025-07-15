package com.expensetracker.expenseservice.controller;

import com.expensetracker.expenseservice.dto.ExpenseRequest;
import com.expensetracker.expenseservice.entity.Expense;
import com.expensetracker.expenseservice.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        expenseRepository.deleteAll();
    }
    
    @Test
    @WithMockUser(username = "1")
    void createExpense_ShouldReturnCreatedExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("25.50"));
        request.setCategory("Food");
        request.setDate(LocalDate.now());
        request.setDescription("Lunch at restaurant");
        request.setCurrency("USD");
        
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
                .andExpect(jsonPath("$.currency").value("USD"));
    }
    
    @Test
    @WithMockUser(username = "1")
    void createExpense_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        // Missing required fields
        
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "1")
    void getExpenseById_ShouldReturnExpense() throws Exception {
        // Create test expense
        Expense expense = new Expense();
        expense.setUserId(1L);
        expense.setAmount(new BigDecimal("25.50"));
        expense.setCategory("Food");
        expense.setDate(LocalDate.now());
        expense.setDescription("Test expense");
        expense.setCurrency("USD");
        
        Expense savedExpense = expenseRepository.save(expense);
        
        mockMvc.perform(get("/api/expenses/{id}", savedExpense.getId()))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.id").value(savedExpense.getId()))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("Food"));
    }
    
    @Test
    @WithMockUser(username = "1")
    void getAllExpenses_ShouldReturnPageOfExpenses() throws Exception {
        // Create test expenses
        for (int i = 0; i < 5; i++) {
            Expense expense = new Expense();
            expense.setUserId(1L);
            expense.setAmount(new BigDecimal("10.00"));
            expense.setCategory("Test");
            expense.setDate(LocalDate.now());
            expense.setDescription("Test expense " + i);
            expense.setCurrency("USD");
            expenseRepository.save(expense);
        }
        
        mockMvc.perform(get("/api/expenses")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(5));
    }
    
    @Test
    void createExpense_ShouldReturnUnauthorized_WhenNoAuth() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("25.50"));
        request.setCategory("Food");
        request.setDate(LocalDate.now());
        request.setDescription("Lunch at restaurant");
        
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
package com.expensetracker.gateway.config;

import com.expensetracker.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes (Public auth endpoints)
                .route("user-service-auth", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8081"))
                
                // User Service Routes (Protected)
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8081"))
                
                // Expense Service Routes (All protected)
                .route("expense-service", r -> r
                        .path("/api/expenses/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8082"))
                
                // Budget Service Routes (All protected)
                .route("budget-service", r -> r
                        .path("/api/budgets/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8083"))
                
                // Currency Service Routes (Protected)
                .route("currency-service", r -> r
                        .path("/api/currency/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:3000"))
                
                // Swagger Documentation Routes (Public)
                .route("user-service-docs", r -> r
                        .path("/user-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                .route("expense-service-docs", r -> r
                        .path("/expense-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                
                .route("budget-service-docs", r -> r
                        .path("/budget-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                
                .route("currency-service-docs", r -> r
                        .path("/currency-service/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:3000"))
                
                // Health Check Routes (Public)
                .route("health-checks", r -> r
                        .path("/health/**", "/actuator/health/**")
                        .uri("http://localhost:8081"))
                
                .build();
    }
}
# Smart Expense Tracker - Testing Guide

## 🚀 **Quick Start Testing**

### **1. Start the System**
```bash
# Build and start all services
docker-compose up --build -d

# Check all services are running
docker-compose ps
```

### **2. Test User Registration & Authentication**
```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# Expected Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "type": "Bearer",
#   "id": 1,
#   "username": "testuser",
#   "email": "test@example.com",
#   "role": "USER"
# }

# Save the token for subsequent requests
export JWT_TOKEN="YOUR_JWT_TOKEN_HERE"
```

### **3. Test Expense Management**
```bash
# Create an expense
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 25.50,
    "category": "Food",
    "date": "2024-01-15",
    "description": "Lunch at restaurant",
    "currency": "USD"
  }'

# Get all expenses
curl -X GET http://localhost:8080/api/expenses \
  -H "Authorization: Bearer $JWT_TOKEN"

# Get expense summary
curl -X GET http://localhost:8080/api/expenses/summary \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **4. Test Budget Management**
```bash
# Create a budget
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "category": "Food",
    "amount": 500.00,
    "month": 1,
    "year": 2024,
    "currency": "USD"
  }'

# Get all budgets
curl -X GET http://localhost:8080/api/budgets \
  -H "Authorization: Bearer $JWT_TOKEN"

# Trigger budget alert check (manual)
curl -X POST http://localhost:8080/api/budgets/admin/trigger-alerts \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **5. Test Currency Conversion**
```bash
# Convert currency
curl -X POST http://localhost:8080/api/currency/convert \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100,
    "fromCurrency": "USD",
    "toCurrency": "EUR"
  }'

# Get exchange rates
curl -X GET http://localhost:8080/api/currency/rates/USD \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## 📊 **API Documentation Access**

### **Swagger UI Endpoints**
- **User Service**: http://localhost:8081/swagger-ui.html
- **Expense Service**: http://localhost:8082/swagger-ui.html
- **Budget Service**: http://localhost:8083/swagger-ui.html
- **Currency Service**: http://localhost:3000/api-docs
- **Via API Gateway**: 
  - http://localhost:8080/user-service/swagger-ui.html
  - http://localhost:8080/expense-service/swagger-ui.html
  - http://localhost:8080/budget-service/swagger-ui.html

## 🔍 **Health Check Endpoints**
```bash
# Check all services health
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/api/expenses/health  # Expense Service
curl http://localhost:8083/actuator/health  # Budget Service
curl http://localhost:3000/health  # Currency Service
curl http://localhost:8080/health  # API Gateway
```

## 🧪 **Automated Testing**

### **Run Unit Tests**
```bash
# Test User Service
cd user-service && mvn test

# Test Expense Service
cd expense-service && mvn test

# Test Budget Service
cd budget-service && mvn test

# Test Currency Service
cd currency-service && npm test
```

### **Integration Testing Scenarios**

#### **Scenario 1: Complete User Journey**
1. Register user → Get JWT token
2. Create expense → Verify creation
3. Create budget for same category → Verify creation
4. Add more expenses to reach 80% of budget → Verify alert
5. Check budget status → Verify spending calculation

#### **Scenario 2: Budget Alert System**
1. Create budget with $100 limit
2. Add expenses totaling $80 → Should trigger alert
3. Check logs for alert notification
4. Add more expenses to exceed budget → Should show over-budget

#### **Scenario 3: Currency Conversion**
1. Create expense in USD
2. Convert to EUR using currency service
3. Verify conversion rates and caching

## 📝 **Sample Test Data**

### **Users**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securepass123"
}
```

### **Expenses**
```json
[
  {
    "amount": 45.99,
    "category": "Food",
    "date": "2024-01-15",
    "description": "Grocery shopping"
  },
  {
    "amount": 12.50,
    "category": "Transportation",
    "date": "2024-01-15",
    "description": "Bus fare"
  },
  {
    "amount": 89.99,
    "category": "Entertainment",
    "date": "2024-01-16",
    "description": "Movie tickets and dinner"
  }
]
```

### **Budgets**
```json
[
  {
    "category": "Food",
    "amount": 200.00,
    "month": 1,
    "year": 2024
  },
  {
    "category": "Transportation",
    "amount": 100.00,
    "month": 1,
    "year": 2024
  },
  {
    "category": "Entertainment",
    "amount": 150.00,
    "month": 1,
    "year": 2024
  }
]
```

## 🐛 **Troubleshooting**

### **Common Issues**

#### **Services Not Starting**
```bash
# Check Docker logs
docker-compose logs [service-name]

# Restart specific service
docker-compose restart [service-name]
```

#### **Database Connection Issues**
```bash
# Check MySQL container
docker-compose logs mysql

# Connect to MySQL directly
docker exec -it expense-tracker-mysql mysql -u root -p
```

#### **JWT Token Issues**
- Ensure token is included in Authorization header
- Check token expiration (24 hours by default)
- Verify token format: `Bearer <token>`

#### **Port Conflicts**
- User Service: 8081
- Expense Service: 8082
- Budget Service: 8083
- API Gateway: 8080
- Currency Service: 3000
- MySQL: 3306

## 📈 **Performance Testing**

### **Load Testing with curl**
```bash
# Test concurrent requests
for i in {1..10}; do
  curl -X GET http://localhost:8080/api/expenses \
    -H "Authorization: Bearer $JWT_TOKEN" &
done
wait
```

### **Database Performance**
```sql
-- Check database performance
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
```

## ✅ **Test Checklist**

### **Functional Tests**
- [ ] User registration works
- [ ] User login returns valid JWT
- [ ] JWT authentication protects endpoints
- [ ] Expense CRUD operations work
- [ ] Budget CRUD operations work
- [ ] Currency conversion works
- [ ] Budget alerts trigger at 80%
- [ ] Scheduled jobs run correctly
- [ ] Health checks return 200 OK

### **Security Tests**
- [ ] Unauthenticated requests return 401
- [ ] Invalid JWT tokens are rejected
- [ ] Users can only access their own data
- [ ] SQL injection protection works
- [ ] CORS is properly configured

### **Integration Tests**
- [ ] Services communicate correctly
- [ ] Database transactions work
- [ ] Error handling is consistent
- [ ] API Gateway routing works
- [ ] Swagger documentation is accessible

## 🎯 **Success Criteria**

The Smart Expense Tracker is fully functional when:
1. ✅ All services start without errors
2. ✅ User can register and login
3. ✅ Expenses can be created, read, updated, deleted
4. ✅ Budgets can be managed and track spending
5. ✅ Currency conversion works with real rates
6. ✅ Budget alerts are sent when thresholds are reached
7. ✅ All API documentation is accessible
8. ✅ Health checks pass for all services

**🎉 Congratulations! Your Smart Expense Tracker is 100% functional!**
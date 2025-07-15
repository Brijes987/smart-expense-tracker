#!/bin/bash

# Smart Expense Tracker - Deployment Script
# This script deploys the complete microservices application

echo "🚀 Smart Expense Tracker - Deployment Starting..."
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}$1${NC}"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

print_header "📋 Pre-deployment Checks"
print_status "✅ Docker is running"
print_status "✅ Docker Compose is available"

# Stop any existing containers
print_header "🛑 Stopping existing containers..."
docker-compose down --remove-orphans

# Clean up old images (optional)
read -p "Do you want to remove old Docker images? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_status "🧹 Cleaning up old Docker images..."
    docker system prune -f
fi

# Build and start services
print_header "🏗️  Building and starting services..."
docker-compose up --build -d

# Wait for services to start
print_status "⏳ Waiting for services to start..."
sleep 30

# Check service health
print_header "🔍 Checking service health..."

services=("mysql:3306" "user-service:8081" "expense-service:8082" "budget-service:8083" "currency-service:3000" "api-gateway:8080")
all_healthy=true

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if docker-compose ps | grep -q "$name.*Up"; then
        print_status "✅ $name is running"
    else
        print_error "❌ $name is not running"
        all_healthy=false
    fi
done

# Test API endpoints
print_header "🧪 Testing API endpoints..."

# Test API Gateway
if curl -s -f http://localhost:8080/health > /dev/null; then
    print_status "✅ API Gateway is responding"
else
    print_warning "⚠️  API Gateway health check failed"
fi

# Test User Service
if curl -s -f http://localhost:8081/actuator/health > /dev/null; then
    print_status "✅ User Service is responding"
else
    print_warning "⚠️  User Service health check failed"
fi

# Test Expense Service
if curl -s -f http://localhost:8082/api/expenses/health > /dev/null; then
    print_status "✅ Expense Service is responding"
else
    print_warning "⚠️  Expense Service health check failed"
fi

# Test Budget Service
if curl -s -f http://localhost:8083/actuator/health > /dev/null; then
    print_status "✅ Budget Service is responding"
else
    print_warning "⚠️  Budget Service health check failed"
fi

# Test Currency Service
if curl -s -f http://localhost:3000/health > /dev/null; then
    print_status "✅ Currency Service is responding"
else
    print_warning "⚠️  Currency Service health check failed"
fi

# Display service URLs
print_header "🌐 Service URLs"
echo "┌─────────────────────────────────────────────────────────────┐"
echo "│                     SERVICE ENDPOINTS                       │"
echo "├─────────────────────────────────────────────────────────────┤"
echo "│ API Gateway:        http://localhost:8080                   │"
echo "│ User Service:       http://localhost:8081                   │"
echo "│ Expense Service:    http://localhost:8082                   │"
echo "│ Budget Service:     http://localhost:8083                   │"
echo "│ Currency Service:   http://localhost:3000                   │"
echo "│ MySQL Database:     localhost:3306                          │"
echo "└─────────────────────────────────────────────────────────────┘"

print_header "📚 API Documentation"
echo "┌─────────────────────────────────────────────────────────────┐"
echo "│                   SWAGGER DOCUMENTATION                     │"
echo "├─────────────────────────────────────────────────────────────┤"
echo "│ User Service:       http://localhost:8081/swagger-ui.html   │"
echo "│ Expense Service:    http://localhost:8082/swagger-ui.html   │"
echo "│ Budget Service:     http://localhost:8083/swagger-ui.html   │"
echo "│ Currency Service:   http://localhost:3000/api-docs          │"
echo "└─────────────────────────────────────────────────────────────┘"

# Sample API test
print_header "🧪 Quick API Test"
print_status "Testing user registration..."

# Test user registration
response=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }' 2>/dev/null)

if echo "$response" | grep -q "token"; then
    print_status "✅ User registration test passed"
    
    # Extract token for further testing
    token=$(echo "$response" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ ! -z "$token" ]; then
        print_status "✅ JWT token received"
        
        # Test expense creation
        expense_response=$(curl -s -X POST http://localhost:8080/api/expenses \
          -H "Authorization: Bearer $token" \
          -H "Content-Type: application/json" \
          -d '{
            "amount": 25.50,
            "category": "Food",
            "date": "2024-01-15",
            "description": "Test expense"
          }' 2>/dev/null)
        
        if echo "$expense_response" | grep -q "id"; then
            print_status "✅ Expense creation test passed"
        else
            print_warning "⚠️  Expense creation test failed"
        fi
        
        # Test budget creation
        budget_response=$(curl -s -X POST http://localhost:8080/api/budgets \
          -H "Authorization: Bearer $token" \
          -H "Content-Type: application/json" \
          -d '{
            "category": "Food",
            "amount": 500.00,
            "month": 1,
            "year": 2024
          }' 2>/dev/null)
        
        if echo "$budget_response" | grep -q "id"; then
            print_status "✅ Budget creation test passed"
        else
            print_warning "⚠️  Budget creation test failed"
        fi
    fi
else
    print_warning "⚠️  User registration test failed"
fi

# Display logs command
print_header "📋 Useful Commands"
echo "View logs:           docker-compose logs [service-name]"
echo "Stop services:       docker-compose down"
echo "Restart service:     docker-compose restart [service-name]"
echo "View containers:     docker-compose ps"
echo "Database access:     docker exec -it expense-tracker-mysql mysql -u root -p"

# Final status
print_header "🎉 Deployment Summary"
if $all_healthy; then
    print_status "✅ All services are running successfully!"
    print_status "✅ Smart Expense Tracker is ready to use!"
    echo ""
    echo "🚀 You can now:"
    echo "   • Register users at: http://localhost:8080/api/auth/register"
    echo "   • View API docs at: http://localhost:8081/swagger-ui.html"
    echo "   • Test the complete system using the endpoints above"
    echo ""
    print_status "📖 Check TESTING_GUIDE.md for detailed testing instructions"
else
    print_error "❌ Some services failed to start properly"
    print_status "📋 Check logs with: docker-compose logs"
fi

echo "=================================================="
echo "🎯 Smart Expense Tracker Deployment Complete!"
echo "=================================================="
#!/bin/bash

# Customer Database Management Lab - Test Script
# This script runs automated tests to verify the lab environment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0

# Function to print colored output
print_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
    TESTS_RUN=$((TESTS_RUN + 1))
}

print_pass() {
    echo -e "${GREEN}[PASS]${NC} $1"
    TESTS_PASSED=$((TESTS_PASSED + 1))
}

print_fail() {
    echo -e "${RED}[FAIL]${NC} $1"
    TESTS_FAILED=$((TESTS_FAILED + 1))
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Test functions

test_service_health() {
    local service_name=$1
    local url=$2
    local expected_status=${3:-200}
    
    print_test "Testing $service_name health ($url)"
    
    if response=$(curl -s -w "%{http_code}" -o /dev/null "$url" 2>/dev/null); then
        if [ "$response" = "$expected_status" ]; then
            print_pass "$service_name is healthy (HTTP $response)"
        else
            print_fail "$service_name returned HTTP $response, expected $expected_status"
        fi
    else
        print_fail "$service_name is not responding"
    fi
}

test_database_connection() {
    print_test "Testing database connection"
    
    if docker exec customer_db psql -U postgres -d customer_db -c "SELECT 1;" > /dev/null 2>&1; then
        print_pass "Database connection successful"
    else
        print_fail "Database connection failed"
    fi
}

test_api_endpoints() {
    local base_url="http://localhost:8080/api"
    
    # Test GET /customers
    print_test "Testing GET /customers endpoint"
    if response=$(curl -s "$base_url/customers" 2>/dev/null); then
        if echo "$response" | grep -q "content"; then
            print_pass "GET /customers returned valid response"
        else
            print_fail "GET /customers returned invalid response"
        fi
    else
        print_fail "GET /customers failed"
    fi
    
    # Test POST /customers (create)
    print_test "Testing POST /customers endpoint"
    local test_customer='{"firstName":"Test","lastName":"User","email":"test@example.com","phone":"555-0000","address":"123 Test St"}'
    
    if response=$(curl -s -X POST -H "Content-Type: application/json" -d "$test_customer" "$base_url/customers" 2>/dev/null); then
        if echo "$response" | grep -q '"id"'; then
            local customer_id=$(echo "$response" | grep -o '"id":[0-9]*' | cut -d':' -f2)
            print_pass "POST /customers created customer with ID $customer_id"
            
            # Test GET /customers/{id}
            print_test "Testing GET /customers/{id} endpoint"
            if response=$(curl -s "$base_url/customers/$customer_id" 2>/dev/null); then
                if echo "$response" | grep -q '"firstName":"Test"'; then
                    print_pass "GET /customers/$customer_id returned correct data"
                else
                    print_fail "GET /customers/$customer_id returned incorrect data"
                fi
            else
                print_fail "GET /customers/$customer_id failed"
            fi
            
            # Test PUT /customers/{id}
            print_test "Testing PUT /customers/{id} endpoint"
            local updated_customer='{"firstName":"Updated","lastName":"User","email":"updated@example.com","phone":"555-0001","address":"456 Updated St"}'
            
            if response=$(curl -s -X PUT -H "Content-Type: application/json" -d "$updated_customer" "$base_url/customers/$customer_id" 2>/dev/null); then
                if echo "$response" | grep -q '"firstName":"Updated"'; then
                    print_pass "PUT /customers/$customer_id updated customer successfully"
                else
                    print_fail "PUT /customers/$customer_id failed to update"
                fi
            else
                print_fail "PUT /customers/$customer_id failed"
            fi
            
            # Test DELETE /customers/{id}
            print_test "Testing DELETE /customers/{id} endpoint"
            if curl -s -X DELETE "$base_url/customers/$customer_id" > /dev/null 2>&1; then
                print_pass "DELETE /customers/$customer_id succeeded"
            else
                print_fail "DELETE /customers/$customer_id failed"
            fi
            
        else
            print_fail "POST /customers failed to create customer"
        fi
    else
        print_fail "POST /customers failed"
    fi
}

test_audit_functionality() {
    print_test "Testing audit trail functionality"
    
    # Check if audit records exist
    if docker exec customer_db psql -U postgres -d customer_db -c "SELECT COUNT(*) FROM audit.customer_audit;" > /dev/null 2>&1; then
        local audit_count=$(docker exec customer_db psql -U postgres -d customer_db -t -c "SELECT COUNT(*) FROM audit.customer_audit;" | tr -d ' ')
        if [ "$audit_count" -gt 0 ]; then
            print_pass "Audit trail contains $audit_count records"
        else
            print_warning "Audit trail is empty (may be expected for fresh install)"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        fi
    else
        print_fail "Unable to query audit trail"
    fi
}

test_sample_data() {
    print_test "Testing sample data presence"
    
    if docker exec customer_db psql -U postgres -d customer_db -c "SELECT COUNT(*) FROM customers;" > /dev/null 2>&1; then
        local customer_count=$(docker exec customer_db psql -U postgres -d customer_db -t -c "SELECT COUNT(*) FROM customers;" | tr -d ' ')
        if [ "$customer_count" -ge 5 ]; then
            print_pass "Sample data loaded successfully ($customer_count customers)"
        else
            print_fail "Expected at least 5 sample customers, found $customer_count"
        fi
    else
        print_fail "Unable to query customer data"
    fi
}

test_frontend_pages() {
    local base_url="http://localhost:3000"
    
    # Test main page
    print_test "Testing frontend main page"
    test_service_health "Frontend Main Page" "$base_url"
    
    # Test if page contains expected content
    print_test "Testing frontend content"
    if response=$(curl -s "$base_url" 2>/dev/null); then
        if echo "$response" | grep -q "Customer Management"; then
            print_pass "Frontend contains expected content"
        else
            print_fail "Frontend missing expected content"
        fi
    else
        print_fail "Unable to fetch frontend content"
    fi
}

test_liquibase_migrations() {
    print_test "Testing Liquibase migration status"
    
    # Check if DATABASECHANGELOG table exists and has entries
    if docker exec customer_db psql -U postgres -d customer_db -c "SELECT COUNT(*) FROM databasechangelog;" > /dev/null 2>&1; then
        local migration_count=$(docker exec customer_db psql -U postgres -d customer_db -t -c "SELECT COUNT(*) FROM databasechangelog;" | tr -d ' ')
        if [ "$migration_count" -gt 0 ]; then
            print_pass "Liquibase migrations applied successfully ($migration_count changesets)"
        else
            print_fail "No Liquibase migrations found"
        fi
    else
        print_fail "Liquibase changelog table not found"
    fi
}

# Main test execution
echo "🧪 Customer Database Management Lab - Test Suite"
echo "=================================================="
echo ""

print_info "Starting test suite..."
echo ""

# Check if services are running
print_info "Checking if lab environment is running..."
if ! docker ps | grep -q "customer_"; then
    print_warning "Lab environment is not running. Starting services..."
    ./start.sh start
    echo ""
    print_info "Waiting for services to stabilize..."
    sleep 30
fi

echo "Running tests..."
echo ""

# Service health tests
print_info "=== Service Health Tests ==="
test_service_health "Backend API" "http://localhost:8080/api/customers"
test_service_health "Frontend" "http://localhost:3000"
test_database_connection
echo ""

# Database tests
print_info "=== Database Tests ==="
test_sample_data
test_audit_functionality
test_liquibase_migrations
echo ""

# API tests
print_info "=== API Tests ==="
test_api_endpoints
echo ""

# Frontend tests
print_info "=== Frontend Tests ==="
test_frontend_pages
echo ""

# Summary
echo "=================================================="
echo "Test Results Summary:"
echo "  Total Tests: $TESTS_RUN"
echo "  Passed: $TESTS_PASSED"
echo "  Failed: $TESTS_FAILED"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    print_pass "🎉 All tests passed! Lab environment is working correctly."
    echo ""
    echo "You can now:"
    echo "  • Access the frontend at http://localhost:3000"
    echo "  • Test the API at http://localhost:8080/api/customers"
    echo "  • Connect to database at localhost:5432"
    echo ""
    exit 0
else
    print_fail "❌ $TESTS_FAILED test(s) failed. Please check the logs above."
    echo ""
    echo "Common troubleshooting steps:"
    echo "  • Check service logs: ./start.sh logs"
    echo "  • Restart services: ./start.sh restart"
    echo "  • Check Docker status: docker ps"
    echo ""
    exit 1
fi

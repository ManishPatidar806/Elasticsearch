#!/bin/bash

# Course Search API Test Script
# This script tests various endpoints of the course search API

BASE_URL="http://localhost:8080/api"
echo "🚀 Testing Course Search API at $BASE_URL"
echo "=================================================="

# Function to make API call and display results
test_endpoint() {
    local name="$1"
    local url="$2"
    local description="$3"
    
    echo
    echo "📍 $name"
    echo "   Description: $description"
    echo "   URL: $url"
    echo "   Response:"
    curl -s "$url" | jq '.' 2>/dev/null || curl -s "$url"
    echo
    echo "---"
}

# Check if jq is available for JSON formatting
if ! command -v jq &> /dev/null; then
    echo "⚠️  jq not found. Install it for better JSON formatting: sudo apt install jq"
    echo
fi

# Test endpoints
echo "🔍 Starting API Tests..."

test_endpoint "Health Check" \
    "$BASE_URL/health" \
    "Check API health and Elasticsearch connection"

test_endpoint "Test Endpoints Documentation" \
    "$BASE_URL/test-endpoints" \
    "Get comprehensive testing guide with sample endpoints"

test_endpoint "Basic Search - All Courses" \
    "$BASE_URL/search" \
    "Get all courses with default pagination"

test_endpoint "Text Search - Music Courses" \
    "$BASE_URL/search?q=music" \
    "Search for courses containing 'music'"

test_endpoint "Fuzzy Search - Misspelled Science" \
    "$BASE_URL/search?q=scince&fuzzy=true" \
    "Fuzzy search for misspelled 'science'"

test_endpoint "Filter by Category - Art" \
    "$BASE_URL/search?category=Art" \
    "Get all courses in Art category"

test_endpoint "Filter by Type - One-Time Courses" \
    "$BASE_URL/search?type=ONE_TIME" \
    "Get all one-time courses"

test_endpoint "Filter by Age Range" \
    "$BASE_URL/search?minAge=8&maxAge=12" \
    "Get courses suitable for ages 8-12"

test_endpoint "Filter by Price Range" \
    "$BASE_URL/search?minPrice=50&maxPrice=100" \
    "Get courses priced between \$50-\$100"

test_endpoint "Filter by Start Date" \
    "$BASE_URL/search?startDate=2025-09-01T00:00:00Z" \
    "Get courses starting from September 1st, 2025"

test_endpoint "Combined Filters" \
    "$BASE_URL/search?q=science&category=Science&minAge=10&maxAge=15&minPrice=20&maxPrice=50" \
    "Complex search with multiple filters"

test_endpoint "Pagination - Second Page" \
    "$BASE_URL/search?page=1&size=5" \
    "Get second page with 5 results per page"

test_endpoint "Sort by Price" \
    "$BASE_URL/search?sort=price" \
    "Sort courses by price (low to high)"

test_endpoint "Search Suggestions" \
    "$BASE_URL/search/suggest?q=mus" \
    "Get search suggestions for partial query 'mus'"

echo
echo "✅ All tests completed!"
echo
echo "📝 Additional Testing Tips:"
echo "   - Use Postman to import these URLs for GUI testing"
echo "   - Paste URLs directly in browser for GET requests"
echo "   - Use httpie: http GET localhost:8080/api/search q==music"
echo "   - Check $BASE_URL/test-endpoints for comprehensive documentation"
echo

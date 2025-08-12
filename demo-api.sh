#!/bin/bash

# Course Search API Demonstration Script
# This script demonstrates all the features of the Course Search Application

echo "=== Course Search Application Demo ==="
echo ""

BASE_URL="http://localhost:8080"

echo "1. Basic text search for 'music':"
curl -s "$BASE_URL/api/search?q=music&size=3" | jq .
echo ""

echo "2. Search with category filter (Math) and price sorting:"
curl -s "$BASE_URL/api/search?category=Math&sort=priceAsc&size=3" | jq .
echo ""

echo "3. Search with age range filter:"
curl -s "$BASE_URL/api/search?minAge=8&maxAge=12&size=3" | jq .
echo ""

echo "4. Search with price range and type filter:"
curl -s "$BASE_URL/api/search?minPrice=20&maxPrice=50&type=ONE_TIME&size=3" | jq .
echo ""

echo "5. Pagination example (page 1, size 2):"
curl -s "$BASE_URL/api/search?q=course&page=1&size=2" | jq .
echo ""

echo "6. Fuzzy search (typo handling) - searching for 'musci' instead of 'music':"
curl -s "$BASE_URL/api/search?q=musci&fuzzy=true&size=3" | jq .
echo ""

echo "7. Autocomplete suggestions for 'Course':"
curl -s "$BASE_URL/api/search/suggest?q=Course&limit=5" | jq .
echo ""

echo "8. Autocomplete suggestions for 'music':"
curl -s "$BASE_URL/api/search/suggest?q=music&limit=3" | jq .
echo ""

echo "9. Date filtering (courses from September 2025 onwards):"
curl -s "$BASE_URL/api/search?startDate=2025-09-01T00:00:00Z&sort=upcoming&size=3" | jq .
echo ""

echo "10. Combined filters: Science courses, age 10-15, price sorted descending:"
curl -s "$BASE_URL/api/search?category=Science&minAge=10&maxAge=15&sort=priceDesc&size=3" | jq .
echo ""

echo "=== Demo Complete ==="

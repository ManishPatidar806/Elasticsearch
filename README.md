# Course Search Application

A Spring Boot application that indexes course documents into Elasticsearch and provides a REST API for searching courses with various filters, pagination, and sorting capabilities.

## Features

### Assignment A (Core Features)
- ✅ Elasticsearch integration with Docker Compose
- ✅ Bulk indexing of sample course data
- ✅ Full-text search on course titles and descriptions
- ✅ Multiple filters (age range, category, type, price, date)
- ✅ Sorting options (upcoming dates, price ascending/descending)
- ✅ Pagination support

### Assignment B (Bonus Features)
- ✅ Autocomplete suggestions for course titles
- ✅ Fuzzy search for handling typos

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6+ (or use included Maven wrapper)

## Quick Start

### 1. Start Elasticsearch

```bash
docker-compose up -d
```

Verify Elasticsearch is running:
```bash
curl http://localhost:9200
```

You should see a JSON response with cluster information.

### 2. Build and Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

The application will:
- Start on `http://localhost:8080`
- Automatically connect to Elasticsearch
- Index all sample courses from `sample-courses.json` on startup
- Be ready to handle search requests

### 3. Verify Data Loading

Check if courses were indexed:
```bash
curl "http://localhost:9200/courses/_count"
```

### 4. Run the Demo Script (Optional)

A demonstration script is provided to showcase all API features:

```bash
./demo-api.sh
```

This script will run through various API examples including search, filters, fuzzy search, and autocomplete.

## API Documentation

### Search Courses

**Endpoint:** `GET /api/search`

**Parameters:**
- `q` (optional) - Search query for title and description
- `minAge` (optional) - Minimum age filter
- `maxAge` (optional) - Maximum age filter
- `category` (optional) - Course category (e.g., "Math", "Science", "Art")
- `type` (optional) - Course type ("ONE_TIME", "COURSE", "CLUB")
- `minPrice` (optional) - Minimum price filter
- `maxPrice` (optional) - Maximum price filter
- `startDate` (optional) - Filter courses starting from this date (ISO-8601 format)
- `sort` (optional) - Sort order: "upcoming" (default), "priceAsc", "priceDesc"
- `page` (optional) - Page number (default: 0)
- `size` (optional) - Page size (default: 10)
- `fuzzy` (optional) - Enable fuzzy search for typos (default: false)

**Response:**
```json
{
  "total": 25,
  "courses": [
    {
      "id": "c-001",
      "title": "Course 1 in Music",
      "description": "This is a detailed description for Course 1 in Music",
      "category": "Art",
      "type": "ONE_TIME",
      "gradeRange": "4th-8th",
      "minAge": 7,
      "maxAge": 11,
      "price": 83.76,
      "nextSessionDate": "2025-09-22T11:27:18Z"
    }
  ]
}
```

### Examples

**Basic text search:**
```bash
curl "http://localhost:8080/api/search?q=music"
```

**Search with filters:**
```bash
curl "http://localhost:8080/api/search?category=Math&minAge=8&maxAge=12&sort=priceAsc"
```

**Search with date filter:**
```bash
curl "http://localhost:8080/api/search?startDate=2025-09-01T00:00:00Z&sort=upcoming"
```

**Pagination:**
```bash
curl "http://localhost:8080/api/search?q=science&page=1&size=5"
```

**Fuzzy search (handles typos):**
```bash
curl "http://localhost:8080/api/search?q=sceince&fuzzy=true"
```

### Autocomplete Suggestions

**Endpoint:** `GET /api/search/suggest`

**Parameters:**
- `q` - Partial course title for suggestions

**Example:**
```bash
curl "http://localhost:8080/api/search/suggest?q=mus"
```

**Response:**
```json
{
  "suggestions": [
    "Course 1 in Music",
    "Course 2 in Music",
    "Course 5 in Music"
  ]
}
```

### Health Check

**Endpoint:** `GET /api/health`

Returns the application and Elasticsearch connection status.

**Example:**
```bash
curl "http://localhost:8080/api/health"
```

**Response:**
```json
{
  "status": "UP",
  "elasticsearch": "connected",
  "totalCourses": 50,
  "timestamp": "2025-08-10T11:54:50.856161897Z"
}
```

## Sample Data

The application includes 50+ sample courses with the following structure:
- **Categories:** Math, Science, Art, History, Music
- **Types:** ONE_TIME, COURSE, CLUB
- **Age ranges:** Various grade levels and age groups
- **Prices:** Range from $15 to $100
- **Session dates:** Spanning several months from current date

## Development

### Running Tests

```bash
./mvnw test
```

### Accessing Kibana (Optional)

Kibana is available at `http://localhost:5601` for Elasticsearch data exploration.

### Project Structure

```
src/
├── main/
│   ├── java/com/undoschool/coursesearch/
│   │   ├── CourseSearchApplication.java      # Main application
│   │   ├── config/DataLoader.java            # Data loading component
│   │   ├── controller/SearchController.java  # REST controllers
│   │   ├── document/CourseDocument.java      # Elasticsearch document
│   │   ├── repository/CourseRepository.java  # Data repository
│   │   └── service/SearchService.java        # Search business logic
│   └── resources/
│       ├── application.properties            # Configuration
│       └── sample-courses.json              # Sample data
└── test/
    └── java/com/undoschool/coursesearch/    # Test cases
```

## Configuration

### Elasticsearch Connection

The application connects to Elasticsearch using these properties in `application.properties`:

```properties
spring.elasticsearch.uris=http://localhost:9200
```

### Docker Configuration

Elasticsearch is configured in `docker-compose.yml`:
- Single-node cluster
- No authentication (development mode)
- Data persistence with Docker volumes
- Memory allocation: 1GB

## Troubleshooting

### Elasticsearch Connection Issues

1. Ensure Docker containers are running:
   ```bash
   docker-compose ps
   ```

2. Check Elasticsearch health:
   ```bash
   curl http://localhost:9200/_cluster/health
   ```

### Application Startup Issues

1. Check if port 8080 is available
2. Verify Java version (17+ required)
3. Ensure Elasticsearch is accessible before starting the app

### Data Not Loading

1. Check application logs for errors
2. Verify `sample-courses.json` is in classpath
3. Check Elasticsearch index exists:
   ```bash
   curl http://localhost:9200/_cat/indices
   ```

## Performance Notes

- The application uses efficient Elasticsearch queries with proper filters
- Bulk indexing is used for initial data loading
- Pagination prevents large result sets from overwhelming the system
- Fuzzy search is configurable to balance accuracy vs. performance

## Technology Stack

- **Spring Boot 3.5.4** - Application framework
- **Spring Data Elasticsearch** - Elasticsearch integration
- **Elasticsearch 8.18.1** - Search engine
- **Jackson** - JSON processing
- **Lombok** - Code generation
- **JUnit 5** - Testing framework

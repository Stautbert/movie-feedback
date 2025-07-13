# Movie Feedback System - Architecture Documentation

## System Overview

The Movie Feedback Collection System is a microservice-based application that allows administrators to manage movie collections and visitors to provide feedback on movies. The system is built using modern technologies and follows microservice architecture principles.

## Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │  Eureka Server  │
│   (React)       │◄──►│   (Spring       │◄──►│   (Service      │
│   Port: 3000    │    │    Cloud)       │    │   Discovery)    │
│                 │    │   Port: 8080    │    │   Port: 8761    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                ┌─────────────────────────────────┐
                │                                 │
        ┌───────▼──────┐                ┌────────▼────────┐
        │ Movie Service│                │ Feedback Service│
        │ (Spring Boot)│                │ (Spring Boot)   │
        │ Port: 8081   │                │ Port: 8082      │
        └──────────────┘                └─────────────────┘
                │                                 │
                ▼                                 ▼
        ┌──────────────┐                ┌─────────────────┐
        │ H2 Database  │                │ H2 Database     │
        │ (In-Memory)  │                │ (In-Memory)     │
        └──────────────┘                └─────────────────┘
```

## Technology Stack

### Backend Services
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (In-Memory for development)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

### Frontend
- **Framework**: React 18
- **Language**: JavaScript/JSX
- **UI Library**: React Bootstrap
- **HTTP Client**: Axios
- **Build Tool**: npm/Node.js
- **Testing**: Jest, React Testing Library

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **Monitoring**: Spring Boot Actuator, Prometheus
- **Security**: Spring Security (basic)

## Microservices Architecture

### 1. Eureka Server (Service Discovery)
- **Port**: 8761
- **Purpose**: Service registration and discovery
- **Features**:
  - Service registry for all microservices
  - Health monitoring
  - Load balancing support

### 2. API Gateway
- **Port**: 8080
- **Purpose**: Single entry point for all client requests
- **Features**:
  - Route requests to appropriate services
  - Load balancing
  - Cross-origin resource sharing (CORS)
  - Request/response transformation

### 3. Movie Service
- **Port**: 8081
- **Purpose**: Manage movie data
- **Features**:
  - CRUD operations for movies
  - Search and filtering capabilities
  - Validation and business logic
  - RESTful API endpoints

### 4. Feedback Service
- **Port**: 8082
- **Purpose**: Handle movie feedback from visitors
- **Features**:
  - CRUD operations for feedback
  - Rating calculations
  - Feedback analytics
  - RESTful API endpoints

### 5. Frontend Application
- **Port**: 3000
- **Purpose**: User interface for both administrators and visitors
- **Features**:
  - Responsive design
  - Admin dashboard
  - Movie browsing
  - Feedback submission
  - Search and filtering

## Data Models

### Movie Entity
```java
@Entity
public class Movie {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private Integer releaseYear;
    private String director;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Feedback Entity
```java
@Entity
public class Feedback {
    private Long id;
    private Long movieId;
    private String visitorName;
    private String comment;
    private Integer rating;
    private String visitorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## API Endpoints

### Movie Service API
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `POST /api/movies` - Create new movie
- `PUT /api/movies/{id}` - Update movie
- `DELETE /api/movies/{id}` - Delete movie
- `GET /api/movies/search?keyword={keyword}` - Search movies
- `GET /api/movies/genre/{genre}` - Get movies by genre
- `GET /api/movies/year/{year}` - Get movies by year
- `GET /api/movies/director/{director}` - Get movies by director

### Feedback Service API
- `GET /api/feedback` - Get all feedback
- `GET /api/feedback/{id}` - Get feedback by ID
- `POST /api/feedback` - Create new feedback
- `PUT /api/feedback/{id}` - Update feedback
- `DELETE /api/feedback/{id}` - Delete feedback
- `GET /api/feedback/movie/{movieId}` - Get feedback by movie
- `GET /api/feedback/visitor/{visitorName}` - Get feedback by visitor
- `GET /api/feedback/rating/{rating}` - Get feedback by rating
- `GET /api/feedback/movie/{movieId}/average-rating` - Get average rating
- `GET /api/feedback/movie/{movieId}/count` - Get feedback count

## Security Considerations

### Current Implementation
- Basic authentication for admin endpoints
- Input validation and sanitization
- CORS configuration
- SQL injection prevention (JPA)

### Recommended Enhancements
- JWT token-based authentication
- Role-based access control (RBAC)
- API rate limiting
- HTTPS enforcement
- Input/output encryption

## Monitoring and Observability

### Health Checks
- Spring Boot Actuator endpoints
- Custom health indicators
- Database connectivity checks

### Metrics
- Prometheus metrics collection
- Custom business metrics
- Performance monitoring

### Logging
- Structured logging with SLF4J
- Log levels configuration
- Request/response logging

## Deployment Options

### Development
- Docker Compose for local development
- In-memory H2 databases
- Hot reloading for frontend

### Production
- Kubernetes deployment
- Persistent database storage
- Load balancers
- Auto-scaling

## Testing Strategy

### Unit Tests
- Service layer testing with Mockito
- Repository layer testing
- Controller layer testing
- Frontend component testing

### Integration Tests
- End-to-end API testing
- Database integration testing
- Service communication testing

### Performance Tests
- Load testing with JMeter
- Stress testing
- Scalability testing

## CI/CD Pipeline

### GitHub Actions Workflow
1. **Test Stage**: Run unit and integration tests
2. **Build Stage**: Build all services and create Docker images
3. **Security Scan**: Vulnerability scanning with Trivy
4. **Deploy Stage**: Deploy to staging/production environments

### Deployment Environments
- **Staging**: Automated deployment from develop branch
- **Production**: Manual deployment from main branch

## Scalability Considerations

### Horizontal Scaling
- Stateless service design
- Load balancer support
- Database connection pooling

### Performance Optimization
- Caching strategies
- Database indexing
- CDN for static assets
- API response optimization

## Future Enhancements

### Planned Features
- User authentication and authorization
- Advanced search and filtering
- Movie recommendations
- Social features (likes, shares)
- Mobile application
- Real-time notifications

### Technical Improvements
- Message queues for async processing
- Event sourcing
- CQRS pattern
- GraphQL API
- Micro-frontend architecture

## Troubleshooting

### Common Issues
1. **Service Discovery Issues**: Check Eureka server connectivity
2. **Database Connection**: Verify database configuration
3. **CORS Issues**: Check API Gateway CORS settings
4. **Build Failures**: Verify Java and Node.js versions

### Debugging Tools
- Spring Boot DevTools
- Browser developer tools
- Docker logs
- Kubernetes logs and events

## Conclusion

The Movie Feedback System demonstrates a well-architected microservice application with proper separation of concerns, scalability considerations, and modern development practices. The system is designed to be maintainable, testable, and deployable across different environments. 
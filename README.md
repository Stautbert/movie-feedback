# Movie Feedback Collection System

A microservice-based application that allows administrators to create movie lists and visitors to add feedback about movies.

## Architecture

The system consists of the following microservices:
- **Movie Service**: Manages movie CRUD operations (admin only)
- **Feedback Service**: Handles movie feedback from visitors
- **API Gateway**: Routes requests to appropriate services
- **Frontend**: React-based UI for both admin and visitor interactions

## Project Structure

```
movie-feedback/
├── backend/
│   ├── movie-service/
│   ├── feedback-service/
│   ├── api-gateway/
│   └── shared/
├── frontend/
├── infrastructure/
│   ├── docker/
│   ├── kubernetes/
│   └── ci-cd/
├── docs/
└── README.md
```

## Functional Requirements

• Administrator should be able to create movie list
• Visitors should be able to add feedback to the movie list

## Non-Functional Requirements

• There should be sufficient test coverage (unit / integration tests) and code quality/security checks.
• Service(s) should be observable, it should have sufficient logs and metrics
• Service(s) should be containerized and deploy-able to Kubernetes
• Automate all stages within a CI/CD pipeline

## Nice to have:
• UI is required for this application.

## Expected Artifacts:
• Architecture diagram and sequence diagram
• Code along with unit and integration test case
• Docker File
• Kubernetes manifest
• Pipeline Script

## Quick Start

1. **Prerequisites:**
   - Java 17+
   - Node.js 18+
   - Docker
   - Kubernetes cluster (minikube/local)

2. **Build and Run:**
   ```bash
   # Build all services
   ./scripts/build.sh
   
   # Run with Docker Compose
   docker-compose up -d
   
   # Deploy to Kubernetes
   kubectl apply -f infrastructure/kubernetes/
   ```

3. **Access the Application:**
   - Frontend: http://localhost:3000
   - API Gateway: http://localhost:8080
   - Admin UI: http://localhost:3000/admin
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

### Option 1: Run with Docker (Recommended)

1. **Prerequisites:**
   - Docker Desktop for Windows/Mac/Linux
   - Download from: https://www.docker.com/products/docker-desktop/

2. **Start the application:**
   ```bash
   # Windows
   scripts\run-docker.bat
   
   # Linux/Mac
   chmod +x scripts/build.sh
   ./scripts/build.sh
   cd infrastructure/docker
   docker compose up -d
   ```

### Option 2: Run Locally (Without Docker)

1. **Prerequisites:**
   - Java 17+ (Download from: https://adoptium.net/)
   - Node.js 18+ (Download from: https://nodejs.org/)
   - Maven (Download from: https://maven.apache.org/download.cgi)

2. **Start the application:**
   ```bash
   # Windows
   scripts\run-local.bat
   
   # Linux/Mac
   # Start each service manually in separate terminals
   ```

### Option 3: Deploy to Kubernetes

1. **Prerequisites:**
   - Kubernetes cluster (minikube, Docker Desktop, or cloud)
   - kubectl CLI tool

2. **Deploy:**
   ```bash
   kubectl apply -f infrastructure/kubernetes/
   ```

## Access the Application

Once started, access the application at:
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Admin UI**: http://localhost:3000/admin
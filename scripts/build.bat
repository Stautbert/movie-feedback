@echo off
setlocal enabledelayedexpansion

echo 🎬 Building Movie Feedback System...

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker and try again.
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed. Please install Maven and try again.
    exit /b 1
)

REM Check if Node.js is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed. Please install Node.js and try again.
    exit /b 1
)

echo [INFO] Building backend services...

REM Build Movie Service
echo [INFO] Building Movie Service...
cd backend\movie-service
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Failed to build Movie Service
    exit /b 1
)
cd ..\..

REM Build Feedback Service
echo [INFO] Building Feedback Service...
cd backend\feedback-service
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Failed to build Feedback Service
    exit /b 1
)
cd ..\..

REM Build API Gateway
echo [INFO] Building API Gateway...
cd backend\api-gateway
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Failed to build API Gateway
    exit /b 1
)
cd ..\..

REM Build Eureka Server
echo [INFO] Building Eureka Server...
cd backend\eureka-server
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Failed to build Eureka Server
    exit /b 1
)
cd ..\..

REM Build frontend
echo [INFO] Building Frontend...
cd frontend
call npm install
if errorlevel 1 (
    echo [ERROR] Failed to install frontend dependencies
    exit /b 1
)
call npm run build
if errorlevel 1 (
    echo [ERROR] Failed to build frontend
    exit /b 1
)
cd ..

REM Build Docker images
echo [INFO] Building Docker images...

REM Build Movie Service image
echo [INFO] Building Movie Service Docker image...
docker build -f infrastructure\docker\Dockerfile.movie-service -t movie-feedback/movie-service:latest .
if errorlevel 1 (
    echo [ERROR] Failed to build Movie Service Docker image
    exit /b 1
)

REM Build Feedback Service image
echo [INFO] Building Feedback Service Docker image...
docker build -f infrastructure\docker\Dockerfile.feedback-service -t movie-feedback/feedback-service:latest .
if errorlevel 1 (
    echo [ERROR] Failed to build Feedback Service Docker image
    exit /b 1
)

REM Build API Gateway image
echo [INFO] Building API Gateway Docker image...
docker build -f infrastructure\docker\Dockerfile.api-gateway -t movie-feedback/api-gateway:latest .
if errorlevel 1 (
    echo [ERROR] Failed to build API Gateway Docker image
    exit /b 1
)

REM Build Eureka Server image
echo [INFO] Building Eureka Server Docker image...
docker build -f infrastructure\docker\Dockerfile.eureka-server -t movie-feedback/eureka-server:latest .
if errorlevel 1 (
    echo [ERROR] Failed to build Eureka Server Docker image
    exit /b 1
)

REM Build Frontend image
echo [INFO] Building Frontend Docker image...
docker build -f infrastructure\docker\Dockerfile.frontend -t movie-feedback/frontend:latest .
if errorlevel 1 (
    echo [ERROR] Failed to build Frontend Docker image
    exit /b 1
)

echo [INFO] Build completed successfully! 🎉

echo.
echo [INFO] Available commands:
echo   docker-compose up -d                    # Start all services
echo   docker-compose down                     # Stop all services
echo   docker-compose logs -f [service-name]   # View logs for a specific service
echo   kubectl apply -f infrastructure/kubernetes/  # Deploy to Kubernetes
echo.
echo [INFO] Services will be available at:
echo   Frontend: http://localhost:3000
echo   API Gateway: http://localhost:8080
echo   Eureka Dashboard: http://localhost:8761
echo   Movie Service: http://localhost:8081
echo   Feedback Service: http://localhost:8082

pause 
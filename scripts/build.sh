#!/bin/bash

# Movie Feedback System Build Script
# This script builds all services and creates Docker images

set -e

echo "🎬 Building Movie Feedback System..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven and try again."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed. Please install Node.js and try again."
    exit 1
fi

# Build backend services
print_status "Building backend services..."

# Build Movie Service
print_status "Building Movie Service..."
cd backend/movie-service
mvn clean package -DskipTests
cd ../..

# Build Feedback Service
print_status "Building Feedback Service..."
cd backend/feedback-service
mvn clean package -DskipTests
cd ../..

# Build API Gateway
print_status "Building API Gateway..."
cd backend/api-gateway
mvn clean package -DskipTests
cd ../..

# Build Eureka Server
print_status "Building Eureka Server..."
cd backend/eureka-server
mvn clean package -DskipTests
cd ../..

# Build frontend
print_status "Building Frontend..."
cd frontend
npm install
npm run build
cd ..

# Build Docker images
print_status "Building Docker images..."

# Build Movie Service image
print_status "Building Movie Service Docker image..."
docker build -f infrastructure/docker/Dockerfile.movie-service -t movie-feedback/movie-service:latest .

# Build Feedback Service image
print_status "Building Feedback Service Docker image..."
docker build -f infrastructure/docker/Dockerfile.feedback-service -t movie-feedback/feedback-service:latest .

# Build API Gateway image
print_status "Building API Gateway Docker image..."
docker build -f infrastructure/docker/Dockerfile.api-gateway -t movie-feedback/api-gateway:latest .

# Build Eureka Server image
print_status "Building Eureka Server Docker image..."
docker build -f infrastructure/docker/Dockerfile.eureka-server -t movie-feedback/eureka-server:latest .

# Build Frontend image
print_status "Building Frontend Docker image..."
docker build -f infrastructure/docker/Dockerfile.frontend -t movie-feedback/frontend:latest .

print_status "Build completed successfully! 🎉"

# Display available commands
echo ""
print_status "Available commands:"
echo "  docker-compose up -d                    # Start all services"
echo "  docker-compose down                     # Stop all services"
echo "  docker-compose logs -f [service-name]   # View logs for a specific service"
echo "  kubectl apply -f infrastructure/kubernetes/  # Deploy to Kubernetes"
echo ""
print_status "Services will be available at:"
echo "  Frontend: http://localhost:3000"
echo "  API Gateway: http://localhost:8080"
echo "  Eureka Dashboard: http://localhost:8761"
echo "  Movie Service: http://localhost:8081"
echo "  Feedback Service: http://localhost:8082" 
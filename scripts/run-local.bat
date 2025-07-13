@echo off
echo 🎬 Starting Movie Feedback System Locally (without Docker)...

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed. Please install Java 17+ and try again.
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

REM Check if Node.js is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed. Please install Node.js 18+ and try again.
    echo Download from: https://nodejs.org/
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed. Please install Maven and try again.
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [INFO] Starting services in separate windows...

REM Start Eureka Server
echo [INFO] Starting Eureka Server...
start "Eureka Server" cmd /k "cd backend\eureka-server && mvn spring-boot:run"

REM Wait a moment for Eureka to start
timeout /t 10 /nobreak >nul

REM Start Movie Service
echo [INFO] Starting Movie Service...
start "Movie Service" cmd /k "cd backend\movie-service && mvn spring-boot:run"

REM Start Feedback Service
echo [INFO] Starting Feedback Service...
start "Feedback Service" cmd /k "cd backend\feedback-service && mvn spring-boot:run"

REM Start API Gateway
echo [INFO] Starting API Gateway...
start "API Gateway" cmd /k "cd backend\api-gateway && mvn spring-boot:run"

REM Wait a moment for backend services to start
timeout /t 15 /nobreak >nul

REM Start Frontend
echo [INFO] Starting Frontend...
start "Frontend" cmd /k "cd frontend && npm install && npm start"

echo.
echo [SUCCESS] Movie Feedback System is starting up!
echo.
echo Services will be available at:
echo   Frontend: http://localhost:3000
echo   API Gateway: http://localhost:8080
echo   Eureka Dashboard: http://localhost:8761
echo   Movie Service: http://localhost:8081
echo   Feedback Service: http://localhost:8082
echo.
echo Each service is running in its own window.
echo Close the windows to stop individual services.
echo.
pause 
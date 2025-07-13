@echo off
setlocal enabledelayedexpansion

echo 🎬 Starting Movie Feedback System Locally (without Docker)...
echo.

REM Check if Java is installed
echo [INFO] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed. Please install Java 17+ and try again.
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
) else (
    echo [OK] Java is installed
)

REM Check if Node.js is installed
echo [INFO] Checking Node.js installation...
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed. Please install Node.js 18+ and try again.
    echo Download from: https://nodejs.org/
    pause
    exit /b 1
) else (
    echo [OK] Node.js is installed
)

REM Check if Maven is installed
echo [INFO] Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed. Please install Maven and try again.
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
) else (
    echo [OK] Maven is installed
)

echo.
echo [INFO] All prerequisites are satisfied. Starting services...
echo.

REM Create a temporary directory for logs
if not exist "logs" mkdir logs

REM Start Eureka Server
echo [INFO] Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d %CD%\backend\eureka-server && echo Starting Eureka Server... && mvn spring-boot:run"

REM Wait for Eureka to start
echo [INFO] Waiting for Eureka Server to start...
timeout /t 15 /nobreak >nul

REM Start Movie Service
echo [INFO] Starting Movie Service...
start "Movie Service" cmd /k "cd /d %CD%\backend\movie-service && echo Starting Movie Service... && mvn spring-boot:run"

REM Start Feedback Service
echo [INFO] Starting Feedback Service...
start "Feedback Service" cmd /k "cd /d %CD%\backend\feedback-service && echo Starting Feedback Service... && mvn spring-boot:run"

REM Wait for services to start
echo [INFO] Waiting for backend services to start...
timeout /t 20 /nobreak >nul

REM Start API Gateway
echo [INFO] Starting API Gateway...
start "API Gateway" cmd /k "cd /d %CD%\backend\api-gateway && echo Starting API Gateway... && mvn spring-boot:run"

REM Wait for API Gateway to start
echo [INFO] Waiting for API Gateway to start...
timeout /t 15 /nobreak >nul

REM Start Frontend
echo [INFO] Starting Frontend...
start "Frontend" cmd /k "cd /d %CD%\frontend && echo Installing dependencies... && npm install && echo Starting Frontend... && npm start"

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
echo [TIP] You can also access the admin interface at: http://localhost:3000/admin
echo [TIP] Check Eureka Dashboard to see all registered services: http://localhost:8761
echo.
pause 
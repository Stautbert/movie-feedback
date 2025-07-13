@echo off
echo 🎬 Starting Movie Feedback System with Docker...

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop and try again.
    echo Download Docker Desktop from: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

REM Navigate to the docker-compose file location
cd "%~dp0..\infrastructure\docker"

REM Try docker compose (newer versions)
docker compose --version >nul 2>&1
if not errorlevel 1 (
    echo [INFO] Using 'docker compose' (newer Docker version)
    docker compose up -d
    goto :success
)

REM Try docker-compose (older versions)
docker-compose --version >nul 2>&1
if not errorlevel 1 (
    echo [INFO] Using 'docker-compose' (older Docker version)
    docker-compose up -d
    goto :success
)

echo [ERROR] Neither 'docker compose' nor 'docker-compose' is available.
echo Please ensure Docker Desktop is properly installed.
pause
exit /b 1

:success
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
echo To stop the services, run: docker compose down
echo To view logs, run: docker compose logs -f [service-name]
echo.
pause 
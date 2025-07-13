@echo off
echo 🧪 Testing Movie Feedback System Setup...
echo.

REM Check if we're in the right directory
if not exist "backend" (
    echo [ERROR] Please run this script from the movie-feedback directory
    pause
    exit /b 1
)

echo [INFO] Checking prerequisites...

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed
    exit /b 1
) else (
    echo [OK] Java is installed
)

REM Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed
    exit /b 1
) else (
    echo [OK] Maven is installed
)

REM Check Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed
    exit /b 1
) else (
    echo [OK] Node.js is installed
)

echo.
echo [INFO] Testing frontend build...

REM Test frontend build
cd frontend
echo [INFO] Installing frontend dependencies...
call npm install
if errorlevel 1 (
    echo [ERROR] Failed to install frontend dependencies
    pause
    exit /b 1
)

echo [INFO] Testing frontend build...
call npm run build
if errorlevel 1 (
    echo [ERROR] Failed to build frontend
    pause
    exit /b 1
)

cd ..

echo.
echo [INFO] Testing backend compilation...

REM Test backend compilation
cd backend\movie-service
echo [INFO] Compiling Movie Service...
call mvn compile -q
if errorlevel 1 (
    echo [ERROR] Failed to compile Movie Service
    pause
    exit /b 1
)
cd ..\..

cd backend\feedback-service
echo [INFO] Compiling Feedback Service...
call mvn compile -q
if errorlevel 1 (
    echo [ERROR] Failed to compile Feedback Service
    pause
    exit /b 1
)
cd ..\..

cd "%~dp0..\backend\api-gateway"
echo [INFO] Compiling API Gateway...
call mvn compile -q
if errorlevel 1 (
    echo [ERROR] Failed to compile API Gateway
    pause
    exit /b 1
)
cd ..\..

cd "%~dp0..\backend\eureka-server"
echo [INFO] Compiling Eureka Server...
call mvn compile -q
if errorlevel 1 (
    echo [ERROR] Failed to compile Eureka Server
    pause
    exit /b 1
)
cd "%~dp0..\scripts"

echo.
echo [SUCCESS] All tests passed! Your setup is ready.
echo.
echo You can now run the application using:
echo   scripts\run-local.bat
echo.
pause 
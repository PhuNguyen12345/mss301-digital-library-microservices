@echo off
if not exist ".env" if "%KEYCLOAK_CLIENT_SECRET%"=="" (
  echo ERROR: KEYCLOAK_CLIENT_SECRET is not configured.
  echo Copy .env.example to .env and set the secret regenerated in Keycloak.
  exit /b 1
)

echo Starting Digilib Microservices...

echo.
echo [1/5] Starting Eureka Server...
start "Eureka Server" cmd /k "cd infra\eureka-server && mvn spring-boot:run"
echo Waiting 15 seconds for Eureka Server to initialize...
timeout /t 15 /nobreak > NUL

echo.
echo [2/5] Starting Config Server...
start "Config Server" cmd /k "cd infra\config-server && mvn spring-boot:run"
echo Waiting 15 seconds for Config Server to initialize...
timeout /t 15 /nobreak > NUL

echo.
echo [3/5] Starting Catalog Service...
start "Catalog Service" cmd /k "cd services\catalog-service && mvn spring-boot:run"

echo.
echo [4/5] Starting Member Service...
start "Member Service" cmd /k "cd services\member-service && mvn spring-boot:run"

echo Waiting 20 seconds for Backend Services to register with Eureka...
timeout /t 20 /nobreak > NUL

echo.
echo [5/5] Starting API Gateway...
start "API Gateway" cmd /k "cd infra\api-gateway && mvn spring-boot:run"

echo.
echo All services have been initiated! They will open in separate terminal windows.
echo You can view the Eureka Dashboard at http://localhost:8761
pause

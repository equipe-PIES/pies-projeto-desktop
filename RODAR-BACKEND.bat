@echo off
cd /d "%~dp0\Pies-Backend"
echo ========================================
echo INICIANDO BACKEND
echo ========================================
echo.
mvn spring-boot:run
pause

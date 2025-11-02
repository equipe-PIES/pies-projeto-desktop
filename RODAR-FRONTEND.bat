@echo off
cd /d "%~dp0\Pies-front"
echo ========================================
echo INICIANDO FRONTEND JAVAFX
echo ========================================
echo.
echo Aguarde a janela abrir...
echo.
mvn javafx:run
pause

@ECHO OFF
REM BFCPEOPTIONSTART
REM Advanced BAT to EXE Converter www.BatToExeConverter.com
REM BFCPEEXE=
REM BFCPEICON=
REM BFCPEICONINDEX=-1
REM BFCPEEMBEDDISPLAY=0
REM BFCPEEMBEDDELETE=1
REM BFCPEADMINEXE=0
REM BFCPEINVISEXE=0
REM BFCPEVERINCLUDE=0
REM BFCPEVERVERSION=1.0.0.0
REM BFCPEVERPRODUCT=Product Name
REM BFCPEVERDESC=Product Description
REM BFCPEVERCOMPANY=Your Company
REM BFCPEVERCOPYRIGHT=Copyright Info
REM BFCPEWINDOWCENTER=1
REM BFCPEDISABLEQE=0
REM BFCPEWINDOWHEIGHT=30
REM BFCPEWINDOWWIDTH=120
REM BFCPEWTITLE=Window Title
REM BFCPEOPTIONEND
@echo off
chcp 65001
title Sistema APAPEQ - Inicializando

echo ========================================
echo    INICIANDO SISTEMA APAPEQ
echo    Aguarde...
echo ========================================

REM 
echo Iniciando Backend (Spring Boot)...
start "Backend APAPEQ" /MIN cmd /c "java -jar "Pies-Backend\target\Pies-Backend-0.0.1-SNAPSHOT.jar" && pause"

REM 
echo Aguardando backend iniciar...
timeout /t 10 /nobreak >nul

REM 
echo Iniciando Frontend...
start "Frontend APAPEQ" /MIN cmd /c "java -jar "Pies-front\target\Pies-front-1.0-SNAPSHOT.jar" && pause"

echo.
echo Sistema APAPEQ iniciado com sucesso!
echo - Backend: http://localhost:8080
echo - Frontend: Em execucao
echo.
echo Pressione ENTER para encerrar ambos...
pause >nul

# Script de teste completo
$ErrorActionPreference = "Stop"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TESTE COMPLETO DO LOGIN" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

try {
    Write-Host "`n1. Testando login..." -ForegroundColor Yellow
    $login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"profbom@gmail.com","password":"senha123"}'
    Write-Host "   OK - Token recebido" -ForegroundColor Green
    
    Write-Host "`n2. Testando /auth/me..." -ForegroundColor Yellow
    $headers = @{"Authorization" = "Bearer $($login.token)"}
    $userInfo = Invoke-RestMethod -Uri "http://localhost:8080/auth/me" -Method GET -Headers $headers
    Write-Host "   OK - UserInfo recebido" -ForegroundColor Green
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "SUCESSO TOTAL!" -ForegroundColor Green  
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "`nDados:" -ForegroundColor Cyan
    $userInfo | ConvertTo-Json
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "AGORA TESTE NO FRONTEND JAVAFX:" -ForegroundColor Yellow
    Write-Host "  Email: profbom@gmail.com" -ForegroundColor White
    Write-Host "  Senha: senha123" -ForegroundColor White
    Write-Host "========================================" -ForegroundColor Cyan
    
} catch {
    Write-Host "`nERRO: $_" -ForegroundColor Red
    Write-Host "O backend pode ainda estar iniciando..." -ForegroundColor Yellow
}

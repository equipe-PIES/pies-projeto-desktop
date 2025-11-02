# Script para testar login completo

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTE COMPLETO DE LOGIN" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n1. Testando backend..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"profbom@gmail.com","password":"senha123"}' -ErrorAction Stop
    Write-Host "   ✓ Login OK - Token recebido" -ForegroundColor Green
    
    $token = $response.token
    $headers = @{"Authorization" = "Bearer $token"}
    
    Write-Host "`n2. Testando /auth/me..." -ForegroundColor Yellow
    $userInfo = Invoke-RestMethod -Uri "http://localhost:8080/auth/me" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "   ✓ /auth/me OK - Role: $($userInfo.role)" -ForegroundColor Green
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "✓✓✓ TUDO FUNCIONANDO! ✓✓✓" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "`nAGORA teste no frontend JavaFX!" -ForegroundColor Yellow
    
} catch {
    Write-Host "   ✗ ERRO: $_" -ForegroundColor Red
    Write-Host "`n⚠️ BACKEND PRECISA SER REINICIADO!" -ForegroundColor Yellow
}

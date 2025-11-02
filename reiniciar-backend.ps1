# Script para reiniciar o backend após mudanças

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "REINICIANDO BACKEND" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

cd "Pies-Backend"

Write-Host "`n1. Compilando..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Compilação OK!" -ForegroundColor Green
    Write-Host "`n2. Iniciando backend..." -ForegroundColor Yellow
    Write-Host "   (Pressione Ctrl+C para parar)" -ForegroundColor Gray
    Write-Host ""
    mvn spring-boot:run
} else {
    Write-Host "`n✗ ERRO na compilação!" -ForegroundColor Red
}

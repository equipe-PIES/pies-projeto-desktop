Write-Host "=== CRIANDO USUÁRIOS COM ROLES CORRETAS ===" -ForegroundColor Cyan
Start-Sleep -Seconds 3

# Professor
Write-Host "`n1. Registrando PROFESSOR..." -ForegroundColor Yellow
$profBody = '{"login":"professor@gmail.com","password":"123","role":"PROFESSOR"}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $profBody -UseBasicParsing
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   Usuário já existe ou erro" -ForegroundColor Yellow
}

# Coordenador
Write-Host "`n2. Registrando COORDENADOR..." -ForegroundColor Yellow
$coordBody = '{"login":"coordenador@gmail.com","password":"123","role":"COORDENADOR"}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $coordBody -UseBasicParsing
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   Usuário já existe ou erro" -ForegroundColor Yellow
}

Write-Host "`n=== CREDENCIAIS CRIADAS ===" -ForegroundColor Green
Write-Host ""
Write-Host "PROFESSOR:" -ForegroundColor Cyan
Write-Host "  Email: professor@gmail.com" -ForegroundColor White
Write-Host "  Senha: 123" -ForegroundColor White
Write-Host ""
Write-Host "COORDENADOR:" -ForegroundColor Cyan
Write-Host "  Email: coordenador@gmail.com" -ForegroundColor White
Write-Host "  Senha: 123" -ForegroundColor White
Write-Host ""

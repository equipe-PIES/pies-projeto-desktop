Write-Host "=== CRIANDO NOVOS USUARIOS ===" -ForegroundColor Cyan
Start-Sleep -Seconds 2

# Professor
Write-Host "`n1. Criando prof.teste@gmail.com..." -ForegroundColor Yellow
$profBody = '{"login":"prof.teste@gmail.com","password":"senha123","role":"professor"}'

try {
    Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $profBody -UseBasicParsing | Out-Null
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   ERRO" -ForegroundColor Red
}

# Coordenador
Write-Host "`n2. Criando coord.teste@gmail.com..." -ForegroundColor Yellow
$coordBody = '{"login":"coord.teste@gmail.com","password":"senha123","role":"coordenador"}'

try {
    Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $coordBody -UseBasicParsing | Out-Null
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   ERRO" -ForegroundColor Red
}

# Testar login professor
Write-Host "`n3. Testando login prof.teste..." -ForegroundColor Yellow
$loginProf = '{"login":"prof.teste@gmail.com","password":"senha123"}'

try {
    $r = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginProf -UseBasicParsing
    Write-Host "   LOGIN OK!" -ForegroundColor Green
} catch {
    Write-Host "   LOGIN FALHOU!" -ForegroundColor Red
}

# Testar login coordenador
Write-Host "`n4. Testando login coord.teste..." -ForegroundColor Yellow
$loginCoord = '{"login":"coord.teste@gmail.com","password":"senha123"}'

try {
    $r = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginCoord -UseBasicParsing
    Write-Host "   LOGIN OK!" -ForegroundColor Green
} catch {
    Write-Host "   LOGIN FALHOU!" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "USE ESTAS CREDENCIAIS NO FRONTEND:" -ForegroundColor Cyan
Write-Host ""
Write-Host "PROFESSOR:" -ForegroundColor Yellow
Write-Host "  Email: prof.teste@gmail.com" -ForegroundColor White
Write-Host "  Senha: senha123" -ForegroundColor White
Write-Host ""
Write-Host "COORDENADOR:" -ForegroundColor Yellow
Write-Host "  Email: coord.teste@gmail.com" -ForegroundColor White
Write-Host "  Senha: senha123" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Green

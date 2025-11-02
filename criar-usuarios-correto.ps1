Write-Host "=== DELETANDO E CRIANDO USUARIOS ===" -ForegroundColor Cyan
Start-Sleep -Seconds 3

# Criar usuario PROFESSOR (com role em minusculo)
Write-Host "`n1. Criando professor2..." -ForegroundColor Yellow
$profBody = '{"login":"professor2@gmail.com","password":"123","role":"professor"}'

try {
    Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $profBody -UseBasicParsing | Out-Null
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   ERRO: Ja existe ou falha" -ForegroundColor Red
}

# Criar usuario COORDENADOR
Write-Host "`n2. Criando coordenador2..." -ForegroundColor Yellow
$coordBody = '{"login":"coordenador2@gmail.com","password":"123","role":"coordenador"}'

try {
    Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $coordBody -UseBasicParsing | Out-Null
    Write-Host "   SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   ERRO: Ja existe ou falha" -ForegroundColor Red
}

# Testar login do professor
Write-Host "`n3. Testando login professor2..." -ForegroundColor Yellow
$loginProf = '{"login":"professor2@gmail.com","password":"123"}'

try {
    $r = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginProf -UseBasicParsing
    Write-Host "   LOGIN OK!" -ForegroundColor Green
    Write-Host "   Token: $($r.Content.Substring(0,50))..." -ForegroundColor Gray
} catch {
    Write-Host "   LOGIN FALHOU!" -ForegroundColor Red
}

# Testar login do coordenador
Write-Host "`n4. Testando login coordenador2..." -ForegroundColor Yellow
$loginCoord = '{"login":"coordenador2@gmail.com","password":"123"}'

try {
    $r = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginCoord -UseBasicParsing
    Write-Host "   LOGIN OK!" -ForegroundColor Green
    Write-Host "   Token: $($r.Content.Substring(0,50))..." -ForegroundColor Gray
} catch {
    Write-Host "   LOGIN FALHOU!" -ForegroundColor Red
}

Write-Host "`n=== USE ESTAS CREDENCIAIS ===" -ForegroundColor Green
Write-Host "professor2@gmail.com / 123" -ForegroundColor Cyan
Write-Host "coordenador2@gmail.com / 123" -ForegroundColor Cyan

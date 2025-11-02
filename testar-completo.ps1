Write-Host "=== TESTE COMPLETO ===" -ForegroundColor Cyan
Start-Sleep -Seconds 5

# 1. Registrar novo usuário
Write-Host "`n1. Registrando novo usuário..." -ForegroundColor Yellow
$registerBody = '{"login":"teste@gmail.com","password":"senha123","role":"USER"}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $registerBody -UseBasicParsing
    Write-Host "   Registro SUCESSO!" -ForegroundColor Green
} catch {
    Write-Host "   Registro ERRO: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. Tentar logar com o usuário recém-criado
Write-Host "`n2. Tentando logar com o usuário recém-criado..." -ForegroundColor Yellow
$loginBody = '{"login":"teste@gmail.com","password":"senha123"}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -UseBasicParsing
    Write-Host "   Login SUCESSO!" -ForegroundColor Green
    Write-Host "   Token: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "   Login ERRO!" -ForegroundColor Red
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    Write-Host "   $($reader.ReadToEnd())" -ForegroundColor Gray
}

# 3. Tentar logar com usuario1@gmail.com
Write-Host "`n3. Tentando logar com usuario1@gmail.com..." -ForegroundColor Yellow
$loginBody2 = '{"login":"usuario1@gmail.com","password":"123"}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginBody2 -UseBasicParsing
    Write-Host "   Login SUCESSO!" -ForegroundColor Green
    Write-Host "   Token: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "   Login ERRO!" -ForegroundColor Red
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    Write-Host "   $($reader.ReadToEnd())" -ForegroundColor Gray
}

Write-Host "`n=== FIM ===" -ForegroundColor Cyan

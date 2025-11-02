Write-Host "=== TESTE COMPLETO DE AUTENTICACAO ===" -ForegroundColor Cyan
Write-Host ""
Start-Sleep -Seconds 3

$email = "profbom@gmail.com"
$senha = "senha123"

Write-Host "1. Registrando usuario: $email" -ForegroundColor Yellow
$regBody = "{`"login`":`"$email`",`"password`":`"$senha`",`"role`":`"professor`"}"

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/register" -Method POST -ContentType "application/json" -Body $regBody -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "   REGISTRO OK!" -ForegroundColor Green
    }
} catch {
    if ($_.Exception.Response.StatusCode.value__ -eq 400) {
        Write-Host "   Usuario ja existe (OK, vamos tentar logar)" -ForegroundColor Yellow
    } else {
        Write-Host "   ERRO: $($_.Exception.Message)" -ForegroundColor Red
        exit
    }
}

Start-Sleep -Seconds 2

Write-Host "`n2. Tentando fazer LOGIN com: $email" -ForegroundColor Yellow
$loginBody = "{`"login`":`"$email`",`"password`":`"$senha`"}"

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -UseBasicParsing
    
    Write-Host "   LOGIN SUCESSO!" -ForegroundColor Green
    $json = $response.Content | ConvertFrom-Json
    Write-Host "   Token recebido: $($json.token.Substring(0,50))..." -ForegroundColor Gray
    
    Write-Host "`n=== TESTE PASSOU! ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "USE NO FRONTEND:" -ForegroundColor Cyan
    Write-Host "  Email: $email" -ForegroundColor White
    Write-Host "  Senha: $senha" -ForegroundColor White
    
} catch {
    Write-Host "   LOGIN FALHOU!" -ForegroundColor Red
    Write-Host "   Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

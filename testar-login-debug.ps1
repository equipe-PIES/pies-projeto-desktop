Write-Host "=== TESTE DE LOGIN ===" -ForegroundColor Cyan
Start-Sleep -Seconds 20

$body = '{"login":"usuario1@gmail.com","password":"123"}'
Write-Host "Testando login..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $body -UseBasicParsing
    Write-Host "SUCESSO!" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "ERRO!" -ForegroundColor Red
    Write-Host "Status: $($_.Exception.Response.StatusCode.value__)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    Write-Host $reader.ReadToEnd()
}

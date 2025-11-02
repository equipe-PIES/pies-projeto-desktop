# Script para testar o módulo de login Backend + Frontend
# Execute este script no PowerShell

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  TESTE DE INTEGRAÇÃO - LOGIN BACKEND/FRONTEND  " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$backendUrl = "http://localhost:8080"

# Função para testar se o backend está rodando
function Test-Backend {
    Write-Host "🔍 Verificando se o backend está rodando..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "$backendUrl/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"test","password":"test"}' -ErrorAction SilentlyContinue
        Write-Host "✅ Backend está rodando na porta 8080" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "❌ Backend NÃO está rodando!" -ForegroundColor Red
        Write-Host "   Execute: mvn spring-boot:run (na pasta Pies-Backend)" -ForegroundColor Yellow
        return $false
    }
}

# Função para registrar usuários de teste
function Register-TestUsers {
    Write-Host ""
    Write-Host "📝 Registrando usuários de teste..." -ForegroundColor Yellow
    
    # Registrar professor
    try {
        $professorBody = @{
            login = "professor@teste.com"
            password = "senha123"
            role = "professor"
        } | ConvertTo-Json
        
        $response = Invoke-WebRequest -Uri "$backendUrl/auth/register" -Method POST -ContentType "application/json" -Body $professorBody -ErrorAction Stop
        Write-Host "✅ Professor registrado: professor@teste.com / senha123" -ForegroundColor Green
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Host "⚠️  Professor já existe: professor@teste.com / senha123" -ForegroundColor Yellow
        } else {
            Write-Host "❌ Erro ao registrar professor: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    
    # Registrar coordenador
    try {
        $coordenadorBody = @{
            login = "coordenador@teste.com"
            password = "senha123"
            role = "coordenador"
        } | ConvertTo-Json
        
        $response = Invoke-WebRequest -Uri "$backendUrl/auth/register" -Method POST -ContentType "application/json" -Body $coordenadorBody -ErrorAction Stop
        Write-Host "✅ Coordenador registrado: coordenador@teste.com / senha123" -ForegroundColor Green
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Host "⚠️  Coordenador já existe: coordenador@teste.com / senha123" -ForegroundColor Yellow
        } else {
            Write-Host "❌ Erro ao registrar coordenador: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

# Função para testar login
function Test-Login {
    param (
        [string]$email,
        [string]$password,
        [string]$expectedRole
    )
    
    Write-Host ""
    Write-Host "🔐 Testando login: $email" -ForegroundColor Yellow
    
    try {
        # Fazer login
        $loginBody = @{
            login = $email
            password = $password
        } | ConvertTo-Json
        
        $loginResponse = Invoke-RestMethod -Uri "$backendUrl/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
        $token = $loginResponse.token
        
        Write-Host "✅ Login bem-sucedido! Token recebido." -ForegroundColor Green
        
        # Testar /auth/me
        $headers = @{
            Authorization = "Bearer $token"
        }
        
        $meResponse = Invoke-RestMethod -Uri "$backendUrl/auth/me" -Method GET -Headers $headers
        
        Write-Host "   📋 Dados do usuário:" -ForegroundColor Cyan
        Write-Host "      ID: $($meResponse.id)" -ForegroundColor White
        Write-Host "      Email: $($meResponse.email)" -ForegroundColor White
        Write-Host "      Role: $($meResponse.role)" -ForegroundColor White
        
        if ($meResponse.role -eq $expectedRole) {
            Write-Host "   ✅ Role correta: $($meResponse.role)" -ForegroundColor Green
        } else {
            Write-Host "   ❌ Role incorreta! Esperado: $expectedRole, Recebido: $($meResponse.role)" -ForegroundColor Red
        }
        
        return $true
    } catch {
        Write-Host "❌ Erro no login: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Função para testar login com credenciais inválidas
function Test-InvalidLogin {
    Write-Host ""
    Write-Host "🔒 Testando login com credenciais inválidas..." -ForegroundColor Yellow
    
    try {
        $loginBody = @{
            login = "invalido@teste.com"
            password = "senhaerrada"
        } | ConvertTo-Json
        
        $response = Invoke-WebRequest -Uri "$backendUrl/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -ErrorAction Stop
        Write-Host "❌ Esperava erro, mas login funcionou!" -ForegroundColor Red
    } catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Host "✅ Corretamente rejeitou credenciais inválidas" -ForegroundColor Green
        } else {
            Write-Host "⚠️  Erro diferente do esperado: $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}

# Executar testes
Write-Host "Iniciando testes..." -ForegroundColor Cyan
Write-Host ""

# 1. Verificar se backend está rodando
if (-not (Test-Backend)) {
    Write-Host ""
    Write-Host "❌ Backend não está rodando. Não é possível continuar os testes." -ForegroundColor Red
    Write-Host ""
    Write-Host "Para iniciar o backend:" -ForegroundColor Yellow
    Write-Host "  1. Abra outro terminal PowerShell" -ForegroundColor White
    Write-Host "  2. Navegue até a pasta Pies-Backend" -ForegroundColor White
    Write-Host "  3. Execute: mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
    exit
}

# 2. Registrar usuários de teste
Register-TestUsers

# 3. Testar login do professor
$professorTestOk = Test-Login -email "professor@teste.com" -password "senha123" -expectedRole "professor"

# 4. Testar login do coordenador
$coordenadorTestOk = Test-Login -email "coordenador@teste.com" -password "senha123" -expectedRole "coordenador"

# 5. Testar login com credenciais inválidas
Test-InvalidLogin

# Resumo dos testes
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "            RESUMO DOS TESTES                   " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

if ($professorTestOk) {
    Write-Host "✅ Login de Professor: PASSOU" -ForegroundColor Green
} else {
    Write-Host "❌ Login de Professor: FALHOU" -ForegroundColor Red
}

if ($coordenadorTestOk) {
    Write-Host "✅ Login de Coordenador: PASSOU" -ForegroundColor Green
} else {
    Write-Host "❌ Login de Coordenador: FALHOU" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "         PRÓXIMO PASSO: TESTAR FRONTEND         " -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Agora teste no frontend JavaFX:" -ForegroundColor Yellow
Write-Host "  1. Abra outro terminal PowerShell" -ForegroundColor White
Write-Host "  2. Navegue até: Pies-front" -ForegroundColor White
Write-Host "  3. Execute: mvn javafx:run" -ForegroundColor White
Write-Host "  4. Na tela de login, use:" -ForegroundColor White
Write-Host ""
Write-Host "     PROFESSOR:" -ForegroundColor Cyan
Write-Host "     Email: professor@teste.com" -ForegroundColor White
Write-Host "     Senha: senha123" -ForegroundColor White
Write-Host ""
Write-Host "     COORDENADOR:" -ForegroundColor Cyan
Write-Host "     Email: coordenador@teste.com" -ForegroundColor White
Write-Host "     Senha: senha123" -ForegroundColor White
Write-Host ""

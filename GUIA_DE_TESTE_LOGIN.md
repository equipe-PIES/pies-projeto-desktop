# 🧪 GUIA DE TESTE - LOGIN BACKEND + FRONTEND

## 📋 Resumo do que foi feito

Criei toda a infraestrutura de autenticação que estava faltando no backend:

### ✅ Arquivos criados:
1. **AuthenticationController.java** - Controller com endpoints `/auth/login`, `/auth/register`, `/auth/me`
2. **TokenService.java** - Serviço para gerar e validar tokens JWT
3. **SecurityFilter.java** - Filtro que intercepta requisições e valida tokens
4. **SecurityConfigurations.java** - Configurações de segurança do Spring Security
5. **AuthorizationService.java** - Serviço que carrega dados do usuário
6. **AuthenticationDTO.java** - DTO para receber credenciais de login
7. **LoginResponseDTO.java** - DTO para retornar o token JWT

---

## 🚀 PASSO A PASSO PARA TESTAR

### **ETAPA 1: Preparar o Backend**

#### 1.1 - Navegar para a pasta do Backend
```powershell
cd "c:\Users\Usuario\Desktop\Estudos UFC\4º semestre\PIES\Projeto-Desktop-ONG\pies-projeto-desktop\Pies-Backend"
```

#### 1.2 - Limpar e compilar o projeto
```powershell
mvn clean install
```

#### 1.3 - Iniciar o backend
```powershell
mvn spring-boot:run
```

**✅ O backend deve iniciar na porta 8080**

Aguarde até ver mensagens como:
```
Started PiesBackendApplication in X seconds
```

---

### **ETAPA 2: Criar um usuário de teste**

Antes de testar o login, você precisa criar um usuário no banco de dados.

#### Opção A: Usando Postman/Insomnia/Thunder Client

**POST** `http://localhost:8080/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "login": "professor@teste.com",
  "password": "senha123",
  "role": "professor"
}
```

**Criar outro usuário coordenador:**
```json
{
  "login": "coordenador@teste.com",
  "password": "senha123",
  "role": "coordenador"
}
```

#### Opção B: Usando PowerShell (curl)

**Professor:**
```powershell
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{\"login\":\"professor@teste.com\",\"password\":\"senha123\",\"role\":\"professor\"}'
```

**Coordenador:**
```powershell
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{\"login\":\"coordenador@teste.com\",\"password\":\"senha123\",\"role\":\"coordenador\"}'
```

---

### **ETAPA 3: Testar o login via API (Backend isolado)**

#### 3.1 - Testar login de professor

**POST** `http://localhost:8080/auth/login`

**Body (JSON):**
```json
{
  "login": "professor@teste.com",
  "password": "senha123"
}
```

**Resposta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3.2 - Testar o endpoint /auth/me

Copie o token recebido e faça:

**GET** `http://localhost:8080/auth/me`

**Headers:**
```
Authorization: Bearer SEU_TOKEN_AQUI
```

**Resposta esperada (200 OK):**
```json
{
  "id": "uuid-do-usuario",
  "name": null,
  "email": "professor@teste.com",
  "role": "professor"
}
```

---

### **ETAPA 4: Preparar o Frontend**

#### 4.1 - Abrir novo terminal PowerShell

#### 4.2 - Navegar para a pasta do Frontend
```powershell
cd "c:\Users\Usuario\Desktop\Estudos UFC\4º semestre\PIES\Projeto-Desktop-ONG\pies-projeto-desktop\Pies-front"
```

#### 4.3 - Limpar e compilar o projeto
```powershell
mvn clean install
```

#### 4.4 - Iniciar o frontend JavaFX
```powershell
mvn javafx:run
```

**✅ A aplicação JavaFX deve abrir mostrando a tela de login**

---

### **ETAPA 5: Testar a integração completa (Backend + Frontend)**

#### 5.1 - Na tela de login, inserir credenciais

**Professor:**
- Email: `professor@teste.com`
- Senha: `senha123`

**Coordenador:**
- Email: `coordenador@teste.com`
- Senha: `senha123`

#### 5.2 - Clicar em "Entrar"

**✅ Resultados esperados:**

**Se for PROFESSOR:**
- Deve redirecionar para `tela-inicio-professor.fxml`
- Deve ver a tela com menu lateral e conteúdo de professor

**Se for COORDENADOR:**
- Deve redirecionar para `tela-inicio-coord.fxml`
- Deve ver a tela com cards de cadastro (Turma, Professor, Aluno)

**Se as credenciais estiverem erradas:**
- Deve mostrar mensagem: "Credenciais inválidas. Tente novamente."

**Se os campos estiverem vazios:**
- Deve mostrar mensagem: "Por favor, preencha E-mail e Senha."

---

## 🔍 COMO VERIFICAR SE ESTÁ FUNCIONANDO

### ✅ Backend funcionando corretamente:
1. Console do backend deve mostrar:
   ```
   Hibernate: select u1_0.id,... from users u1_0 where u1_0.email=?
   ```
2. Requisição POST para `/auth/login` retorna token
3. Requisição GET para `/auth/me` com token retorna dados do usuário

### ✅ Frontend funcionando corretamente:
1. Ao clicar em "Entrar", o console do IDE deve mostrar logs de conexão HTTP
2. Se houver erro, verá mensagem no console:
   ```
   Erro ao fazer login: Connection refused
   ```
3. Se funcionar, a tela muda automaticamente

### ✅ Integração funcionando:
1. Login com professor abre tela de professor
2. Login com coordenador abre tela de coordenador
3. Login com credenciais inválidas mostra erro

---

## 🐛 PROBLEMAS COMUNS E SOLUÇÕES

### Problema 1: "Connection refused" ou "ConnectException"
**Causa:** Backend não está rodando ou rodando em porta diferente

**Solução:**
1. Verificar se o backend está rodando: `netstat -ano | findstr :8080`
2. Se não estiver, iniciar o backend: `mvn spring-boot:run`

### Problema 2: "401 Unauthorized" no /auth/me
**Causa:** Token inválido ou expirado

**Solução:**
1. Fazer login novamente para gerar novo token
2. Token expira em 2 horas

### Problema 3: "Credenciais inválidas" mesmo com dados corretos
**Causa:** Usuário não existe no banco ou senha está errada

**Solução:**
1. Criar o usuário novamente usando `/auth/register`
2. Verificar se o backend conseguiu conectar ao banco de dados Supabase

### Problema 4: Tela não muda após login bem-sucedido
**Causa:** Arquivos FXML não encontrados ou erro no caminho

**Solução:**
1. Verificar se os arquivos existem:
   - `tela-inicio-professor.fxml`
   - `tela-inicio-coord.fxml`
2. Verificar console do frontend para ver stack trace do erro

### Problema 5: Backend não inicia - "Port 8080 already in use"
**Causa:** Outra aplicação está usando a porta 8080

**Solução:**
```powershell
# Encontrar processo usando porta 8080
netstat -ano | findstr :8080

# Matar o processo (substitua PID pelo número encontrado)
taskkill /PID NUMERO_DO_PID /F
```

---

## 📊 FLUXO COMPLETO DA AUTENTICAÇÃO

```
┌─────────────┐
│   FRONTEND  │
│  (JavaFX)   │
└──────┬──────┘
       │
       │ 1. Usuário digita email e senha
       │    Clica em "Entrar"
       │
       ▼
┌──────────────────────────────────────┐
│ LoginController.handleLoginButtonAction()
│ - Valida campos
│ - Chama authService.authenticate()
└──────┬────────────────────────────────┘
       │
       │ 2. authService faz requisição HTTP
       │    POST http://localhost:8080/auth/login
       │    Body: {"login": "...", "password": "..."}
       │
       ▼
┌──────────────────────────────────────┐
│   BACKEND (Spring Boot)              │
│   AuthenticationController           │
│   - Valida credenciais              │
│   - Gera token JWT                  │
│   - Retorna {"token": "..."}        │
└──────┬────────────────────────────────┘
       │
       │ 3. Frontend armazena token
       │    Faz requisição GET /auth/me
       │    Header: Authorization: Bearer TOKEN
       │
       ▼
┌──────────────────────────────────────┐
│   BACKEND                            │
│   - SecurityFilter valida token     │
│   - AuthenticationController        │
│     retorna dados do usuário        │
│   - {"id", "name", "email", "role"} │
└──────┬────────────────────────────────┘
       │
       │ 4. Frontend recebe role do usuário
       │    Decide qual tela abrir
       │
       ▼
┌──────────────────────────────────────┐
│  Se role = "professor"               │
│  → Abre tela-inicio-professor.fxml   │
│                                      │
│  Se role = "coordenador"             │
│  → Abre tela-inicio-coord.fxml       │
└──────────────────────────────────────┘
```

---

## 🎯 CHECKLIST DE TESTE COMPLETO

### Backend Isolado:
- [ ] Backend inicia sem erros
- [ ] Consegue registrar usuário via `/auth/register`
- [ ] Consegue fazer login via `/auth/login` e receber token
- [ ] Consegue acessar `/auth/me` com token e receber dados do usuário
- [ ] Token inválido retorna 401

### Frontend Isolado:
- [ ] Frontend compila sem erros
- [ ] Tela de login aparece corretamente
- [ ] Campos de email e senha funcionam
- [ ] Validação de campos vazios funciona
- [ ] Mensagens de erro aparecem corretamente

### Integração:
- [ ] Login com professor funciona e abre tela correta
- [ ] Login com coordenador funciona e abre tela correta
- [ ] Login com credenciais inválidas mostra erro
- [ ] Campos vazios são validados antes de enviar ao backend
- [ ] Troca de tela acontece automaticamente após login bem-sucedido

---

## 📝 NOTAS IMPORTANTES

1. **Banco de Dados:** O backend está configurado para usar PostgreSQL no Supabase. Certifique-se de que:
   - A conexão com o banco está funcionando
   - As tabelas foram criadas (Spring JPA faz isso automaticamente no primeiro run)

2. **Token JWT:** 
   - Token expira em 2 horas
   - Token é armazenado apenas em memória no `AuthService` (não persiste entre sessões)

3. **Senha:** 
   - Senhas são criptografadas com BCrypt
   - Nunca compare senhas em texto puro

4. **Roles disponíveis:**
   - `professor`
   - `coordenador`
   - `admin`
   - `user`

---

## 🎉 PRÓXIMOS PASSOS

Depois de confirmar que o login funciona, você pode:

1. **Adicionar nome ao usuário:** Atualmente o campo `name` fica null. Você pode:
   - Adicionar campo de nome no registro
   - Criar endpoint para atualizar perfil

2. **Persistir sessão:** Implementar salvamento do token para:
   - Usuário não precisar fazer login toda vez
   - Usar SharedPreferences ou arquivo local

3. **Adicionar logout:** Criar botão de sair que:
   - Limpa o token armazenado
   - Volta para tela de login

4. **Testes automatizados:** Criar testes unitários e de integração

---

**Boa sorte com os testes! 🚀**

# Sistema de Roles - Professor e Coordenador

## Visão Geral

O sistema foi implementado com controle de acesso baseado em roles, onde:
- **Professores** podem acessar apenas rotas específicas de professor
- **Coordenadores** podem acessar apenas rotas específicas de coordenador
- **Admins** mantêm acesso total ao sistema
- **Usuários** têm acesso básico

## ✅ Requisitos Atendidos

### 1. Múltiplos Professores e Um Coordenador
- ✅ **Sistema permite múltiplos professores**: Não há restrições de quantidade no registro
- ✅ **Sistema permite um coordenador**: Pode ser registrado normalmente
- ✅ **Validação única por email**: Apenas verifica se o email já existe

### 2. Isolamento de Acesso
- ✅ **Professor NÃO acessa rotas de coordenador**: Configuração de segurança bloqueia
- ✅ **Coordenador NÃO acessa rotas de professor**: Configuração de segurança bloqueia
- ✅ **Controle granular**: Cada endpoint tem verificação de role específica

## Roles Disponíveis

1. **ADMIN** - Acesso total ao sistema
2. **USER** - Acesso básico
3. **PROFESSOR** - Acesso às funcionalidades de professor
4. **COORDENADOR** - Acesso às funcionalidades de coordenador

## Endpoints Implementados

### Autenticação
- `POST /auth/login` - Login (público)
- `POST /auth/register` - Registro (público)
- `GET /auth/me` - Informações do usuário logado (autenticado)

### Professor
- `GET /professor/dashboard` - Dashboard do professor
- `GET /professor/turmas` - Lista de turmas
- `GET /professor/notas` - Sistema de notas
- `GET /professor/perfil` - Perfil do professor

### Coordenador
- `GET /coordenador/dashboard` - Dashboard do coordenador
- `GET /coordenador/professores` - Lista de professores
- `GET /coordenador/relatorios` - Relatórios de gestão
- `GET /coordenador/calendario` - Calendário acadêmico
- `GET /coordenador/perfil` - Perfil do coordenador

### Teste
- `GET /test/public` - Endpoint público (sem autenticação)
- `GET /test/authenticated` - Endpoint autenticado
- `GET /test/admin-only` - Apenas para ADMIN
- `GET /test/professor-only` - Apenas para PROFESSOR
- `GET /test/coordenador-only` - Apenas para COORDENADOR

## Como Testar

### 1. Registrar um Professor
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "professor@test.com",
    "password": "123456",
    "role": "PROFESSOR"
  }'
```

### 2. Registrar um Coordenador
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "coordenador@test.com",
    "password": "123456",
    "role": "COORDENADOR"
  }'
```

### 3. Fazer Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "professor@test.com",
    "password": "123456"
  }'
```

### 4. Usar o Token para Acessar Rotas
```bash
# Acessar dashboard do professor (deve funcionar)
curl -X GET http://localhost:8080/professor/dashboard \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"

# Tentar acessar dashboard do coordenador (deve falhar)
curl -X GET http://localhost:8080/coordenador/dashboard \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## Controle de Acesso

### Configuração de Segurança
- Rotas de professor (`/professor/**`) - apenas usuários com role `PROFESSOR`
- Rotas de coordenador (`/coordenador/**`) - apenas usuários com role `COORDENADOR`
- Rotas de admin - apenas usuários com role `ADMIN`
- Outras rotas - usuários autenticados

### Isolamento de Roles
- Um professor **NÃO** pode acessar rotas de coordenador
- Um coordenador **NÃO** pode acessar rotas de professor
- Apenas admins podem acessar todas as rotas

## Estrutura do Token JWT

O token JWT contém:
- **Subject**: Email do usuário
- **Issuer**: "login-auth-api"
- **Expiration**: 2 horas
- **Authorities**: Roles do usuário (ROLE_PROFESSOR, ROLE_COORDENADOR, etc.)

## Próximos Passos

1. **Frontend**: Implementar interface JavaFX que:
   - Faça login e obtenha o token
   - Verifique a role do usuário
   - Mostre telas específicas baseadas na role
   - Gerencie o token para requisições

2. **Melhorias**:
   - Adicionar mais funcionalidades específicas por role
   - Implementar refresh token
   - Adicionar logs de auditoria
   - Melhorar tratamento de erros

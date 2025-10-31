# Documentação da API de Professores

## Visão Geral

A API de Professores foi implementada com funcionalidades completas de CRUD (Create, Read, Update, Delete) para gerenciamento de professores no sistema PIES. A implementação inclui validações, controle de acesso baseado em roles e endpoints para diferentes operações.

## Estrutura Implementada

### 1. Modelo Professor (`Professor.java`)
- **Campos implementados:**
  - `id`: Identificador único (UUID)
  - `nome`: Nome completo do professor
  - `cpf`: CPF no formato 000.000.000-00
  - `dataNascimento`: Data de nascimento (LocalDate)
  - `genero`: Gênero do professor
  - `formacao`: Formação acadêmica
  - `observacoes`: Observações adicionais (opcional)

- **Validações:**
  - Nome, CPF, data de nascimento, gênero e formação são obrigatórios
  - CPF deve estar no formato correto
  - Data de nascimento deve ser no passado

### 2. Repository (`ProfessorRepository.java`)
- **Métodos implementados:**
  - `findByCpf(String cpf)`: Busca professor por CPF
  - `existsByCpf(String cpf)`: Verifica se CPF já existe
  - `findByNomeContainingIgnoreCase(String nome)`: Busca por nome (parcial)
  - `findByGenero(String genero)`: Busca por gênero
  - `findByFormacao(String formacao)`: Busca por formação
  - `findAllByOrderByNomeAsc()`: Lista todos ordenados por nome

### 3. DTOs
- **ProfessorDTO**: Para respostas da API
- **CreateProfessorDTO**: Para criação de novos professores
- **UpdateProfessorDTO**: Para atualização de professores existentes

### 4. Controller (`ProfessorController.java`)
- **Base URL**: `/professores`

## Endpoints Disponíveis

### 1. Listar Todos os Professores
```
GET /professores
```
- **Permissões**: ADMIN, COORDENADOR
- **Resposta**: Lista de todos os professores ordenados por nome

### 2. Buscar Professor por ID
```
GET /professores/{id}
```
- **Permissões**: ADMIN, COORDENADOR, PROFESSOR
- **Resposta**: Dados do professor ou 404 se não encontrado

### 3. Buscar Professores por Nome
```
GET /professores/buscar?nome={nome}
```
- **Permissões**: ADMIN, COORDENADOR
- **Resposta**: Lista de professores que contêm o nome informado

### 4. Buscar Professores por Gênero
```
GET /professores/genero/{genero}
```
- **Permissões**: ADMIN, COORDENADOR
- **Resposta**: Lista de professores do gênero informado

### 5. Buscar Professores por Formação
```
GET /professores/formacao/{formacao}
```
- **Permissões**: ADMIN, COORDENADOR
- **Resposta**: Lista de professores com a formação informada

### 6. Criar Novo Professor
```
POST /professores
```
- **Permissões**: ADMIN, COORDENADOR
- **Body**: CreateProfessorDTO
- **Validações**: CPF único, campos obrigatórios
- **Resposta**: Dados do professor criado ou erro de validação

### 7. Atualizar Professor
```
PUT /professores/{id}
```
- **Permissões**: ADMIN, COORDENADOR
- **Body**: UpdateProfessorDTO
- **Validações**: CPF único (se alterado), campos obrigatórios
- **Resposta**: Dados atualizados do professor ou 404 se não encontrado

### 8. Remover Professor
```
DELETE /professores/{id}
```
- **Permissões**: ADMIN
- **Resposta**: Confirmação de remoção ou 404 se não encontrado

## Exemplos de Uso

### Criar um Professor
```json
POST /professores
{
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "dataNascimento": "1980-05-15",
    "genero": "Masculino",
    "formacao": "Doutorado em Ciência da Computação",
    "observacoes": "Especialista em Inteligência Artificial"
}
```

### Atualizar um Professor
```json
PUT /professores/{id}
{
    "nome": "João Silva Santos",
    "cpf": "123.456.789-00",
    "dataNascimento": "1980-05-15",
    "genero": "Masculino",
    "formacao": "Doutorado em Ciência da Computação",
    "observacoes": "Especialista em Inteligência Artificial e Machine Learning"
}
```

## Controle de Acesso

- **ADMIN**: Acesso total a todas as operações
- **COORDENADOR**: Pode criar, listar, buscar e atualizar professores
- **PROFESSOR**: Pode apenas visualizar seu próprio perfil

## Validações Implementadas

1. **CPF**: Formato obrigatório (000.000.000-00) e único no sistema
2. **Nome**: Campo obrigatório
3. **Data de Nascimento**: Obrigatória e deve ser no passado
4. **Gênero**: Campo obrigatório
5. **Formação**: Campo obrigatório
6. **Observações**: Campo opcional

## Tratamento de Erros

- **400 Bad Request**: Dados inválidos ou CPF duplicado
- **404 Not Found**: Professor não encontrado
- **403 Forbidden**: Acesso negado por falta de permissão
- **401 Unauthorized**: Token de autenticação inválido

## Banco de Dados

A tabela `professores` será criada automaticamente pelo JPA com os seguintes campos:
- `id` (VARCHAR, Primary Key)
- `nome` (VARCHAR, Not Null)
- `cpf` (VARCHAR, Not Null, Unique)
- `data_nascimento` (DATE, Not Null)
- `genero` (VARCHAR, Not Null)
- `formacao` (VARCHAR, Not Null)
- `observacoes` (VARCHAR, Nullable)

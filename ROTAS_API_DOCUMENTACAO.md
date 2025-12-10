# Documentação das Rotas da API - Funcionalidades Implementadas

## 📋 Índice
1. [Atribuição de userId ao Professor](#1-atribuição-de-userid-ao-professor)
2. [Geração de PDF do Relatório Final](#2-geração-de-pdf-do-relatório-final)

---

## 1. Atribuição de userId ao Professor

### 1.1. Criar Professor com userId (POST)

**Endpoint:** `POST /professores`

**Descrição:** Cria um novo professor e permite vincular um userId opcional durante a criação.

**Permissões:** ADMIN, COORDENADOR

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "dataNascimento": "1980-05-15",
  "genero": "MASCULINO",
  "formacao": "Doutorado em Educação",
  "observacoes": "Especialista em educação inclusiva",
  "userId": "uuid-do-usuario-opcional"
}
```

**Resposta de Sucesso (201 Created):**
```json
{
  "id": "uuid-do-professor",
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "dataNascimento": "1980-05-15",
  "genero": "MASCULINO",
  "formacao": "Doutorado em Educação",
  "observacoes": "Especialista em educação inclusiva",
  "userId": "uuid-do-usuario"
}
```

**Resposta de Erro (400 Bad Request):**
- CPF duplicado: `"Já existe um professor cadastrado com o CPF: 123.456.789-00"`
- Usuário não encontrado: `"Usuário com ID {userId} não encontrado."`

**Exemplo de Teste (cURL):**
```bash
curl -X POST http://localhost:8080/professores \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "dataNascimento": "1980-05-15",
    "genero": "MASCULINO",
    "formacao": "Doutorado em Educação",
    "userId": "uuid-do-usuario"
  }'
```

---

### 1.2. Atualizar userId de um Professor (PUT)

**Endpoint:** `PUT /professores/{id}/userId`

**Descrição:** Atualiza o userId de um professor existente.

**Permissões:** ADMIN, COORDENADOR

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
"uuid-do-usuario"
```

**Resposta de Sucesso (200 OK):**
```
"UserId atualizado com sucesso"
```

**Resposta de Erro:**
- 404 Not Found: Professor não encontrado

**Exemplo de Teste (cURL):**
```bash
curl -X PUT http://localhost:8080/professores/{professor-id}/userId \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '"uuid-do-usuario"'
```

---

### 1.3. Buscar Professor por ID (GET)

**Endpoint:** `GET /professores/{id}`

**Descrição:** Busca um professor específico e retorna seus dados, incluindo o userId se estiver vinculado.

**Permissões:** ADMIN, COORDENADOR, PROFESSOR

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "id": "uuid-do-professor",
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "dataNascimento": "1980-05-15",
  "genero": "MASCULINO",
  "formacao": "Doutorado em Educação",
  "observacoes": "Especialista em educação inclusiva",
  "userId": "uuid-do-usuario",
  "turmasIds": []
}
```

**Exemplo de Teste (cURL):**
```bash
curl -X GET http://localhost:8080/professores/{professor-id} \
  -H "Authorization: Bearer {seu-token}"
```

---

## 2. Geração de PDF do Relatório Final

### 2.1. Baixar PDF do Relatório Individual (GET)

**Endpoint:** `GET /api/relatorios-individuais/{id}/pdf`

**Descrição:** Gera e retorna um PDF profissional e formatado do relatório individual do educando.

**Permissões:** Requer autenticação (qualquer role autenticada)

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta de Sucesso (200 OK):**
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="Relatorio_Final_{nome-educando}_{id}.pdf"`
- Body: Arquivo PDF binário

**Resposta de Erro:**
- 404 Not Found: Relatório não encontrado
- 500 Internal Server Error: Erro na geração do PDF

**Características do PDF Gerado:**
- ✅ Cabeçalho profissional com título e subtítulo
- ✅ Seção de informações gerais (Educando, Professor, Data)
- ✅ Todas as seções do relatório formatadas:
  - Dados Funcionais
  - Funcionalidade Cognitiva
  - Alfabetização e Letramento
  - Adaptações Curriculares
  - Participação nas Atividades Propostas
  - Autonomia
  - Interação com a Professora
  - Atividades de Vida Diária (AVDs)
- ✅ Rodapé com informações do documento
- ✅ Formatação profissional com cores e espaçamento adequado

**Exemplo de Teste (cURL):**
```bash
curl -X GET http://localhost:8080/api/relatorios-individuais/{relatorio-id}/pdf \
  -H "Authorization: Bearer {seu-token}" \
  --output relatorio.pdf
```

**Exemplo de Teste (Postman/Insomnia):**
1. Método: GET
2. URL: `http://localhost:8080/api/relatorios-individuais/{relatorio-id}/pdf`
3. Headers: `Authorization: Bearer {seu-token}`
4. Salvar resposta como arquivo PDF

---

### 2.2. Buscar Relatório por ID (GET)

**Endpoint:** `GET /api/relatorios-individuais/{id}`

**Descrição:** Busca um relatório individual específico para obter o ID necessário para gerar o PDF.

**Permissões:** Requer autenticação

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "id": "uuid-do-relatorio",
  "educandoId": "uuid-do-educando",
  "educandoNome": "Maria Santos",
  "professorId": "uuid-do-professor",
  "professorNome": "João Silva",
  "dataCriacao": "2024-01-15T10:30:00",
  "dadosFuncionais": "Texto dos dados funcionais...",
  "funcionalidadeCognitiva": "Texto da funcionalidade cognitiva...",
  "alfabetizacaoLetramento": "Texto sobre alfabetização...",
  "adaptacoesCurriculares": "Texto sobre adaptações...",
  "participacaoAtividades": "Texto sobre participação...",
  "autonomia": "Texto sobre autonomia...",
  "interacaoProfessora": "Texto sobre interação...",
  "atividadesVidaDiaria": "Texto sobre AVDs..."
}
```

**Exemplo de Teste (cURL):**
```bash
curl -X GET http://localhost:8080/api/relatorios-individuais/{relatorio-id} \
  -H "Authorization: Bearer {seu-token}"
```

---

### 2.3. Listar Relatórios por Educando (GET)

**Endpoint:** `GET /api/relatorios-individuais/educando/{educandoId}`

**Descrição:** Lista todos os relatórios de um educando específico. Útil para encontrar o relatório mais recente.

**Permissões:** Requer autenticação

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta de Sucesso (200 OK):**
```json
[
  {
    "id": "uuid-do-relatorio-1",
    "educandoId": "uuid-do-educando",
    "educandoNome": "Maria Santos",
    "professorId": "uuid-do-professor",
    "professorNome": "João Silva",
    "dataCriacao": "2024-01-15T10:30:00",
    ...
  },
  {
    "id": "uuid-do-relatorio-2",
    ...
  }
]
```

**Exemplo de Teste (cURL):**
```bash
curl -X GET http://localhost:8080/api/relatorios-individuais/educando/{educando-id} \
  -H "Authorization: Bearer {seu-token}"
```

---

## 🔐 Autenticação

Todas as rotas requerem autenticação via JWT. Para obter o token:

**Endpoint:** `POST /auth/login`

**Body:**
```json
{
  "login": "email@exemplo.com",
  "password": "senha123"
}
```

**Resposta:**
```json
{
  "token": "jwt-token-aqui"
}
```

---

## 📝 Notas Importantes

1. **userId é opcional**: Ao criar um professor, o campo `userId` é opcional. Se fornecido, o sistema valida se o usuário existe antes de vincular.

2. **Atualização de userId**: O userId pode ser atualizado posteriormente usando o endpoint `PUT /professores/{id}/userId`.

3. **PDF gerado dinamicamente**: O PDF é gerado em tempo real a partir dos dados do relatório. Qualquer alteração no relatório será refletida no PDF.

4. **Nome do arquivo PDF**: O nome do arquivo é gerado automaticamente baseado no nome do educando e ID do relatório, removendo caracteres especiais.

5. **Formato de data**: Use o formato ISO-8601 para datas: `YYYY-MM-DD` (ex: `2024-01-15`).

---

## 🧪 Fluxo de Teste Recomendado

### Teste 1: Atribuição de userId ao Professor

1. **Criar um usuário:**
   ```bash
   POST /auth/register
   Body: { "login": "professor@teste.com", "password": "senha123", "role": "PROFESSOR" }
   ```
   Anote o `userId` retornado.

2. **Criar professor com userId:**
   ```bash
   POST /professores
   Body: { ..., "userId": "userId-obtido-no-passo-1" }
   ```

3. **Verificar vinculação:**
   ```bash
   GET /professores/{professor-id}
   ```
   Verifique se o campo `userId` está presente e correto.

### Teste 2: Geração de PDF

1. **Buscar relatórios do educando:**
   ```bash
   GET /api/relatorios-individuais/educando/{educando-id}
   ```
   Anote o `id` do relatório mais recente.

2. **Baixar PDF:**
   ```bash
   GET /api/relatorios-individuais/{relatorio-id}/pdf
   ```
   Salve o arquivo e verifique se está formatado corretamente.

---

## ✅ Checklist de Validação

- [ ] Professor criado com userId vinculado corretamente
- [ ] userId pode ser atualizado via endpoint PUT
- [ ] PDF é gerado com todas as seções do relatório
- [ ] PDF possui formatação profissional
- [ ] Nome do arquivo PDF é gerado corretamente
- [ ] Todas as informações do educando aparecem no PDF
- [ ] Data e hora são formatadas corretamente no PDF

---

**Desenvolvido com ❤️ para o Sistema PIES**


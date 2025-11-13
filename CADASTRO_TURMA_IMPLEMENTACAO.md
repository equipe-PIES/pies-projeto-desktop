# Implementação do Cadastro de Turma - Front-end Conectado ao Back-end

## Resumo
Este documento descreve a implementação funcional do cadastro de turma no sistema, conectando o front-end JavaFX ao back-end Spring Boot, seguindo o padrão do cadastro de professor.

## Arquivos Criados/Modificados

### 1. DTOs Criados

#### `CreateTurmaDTO.java`
- **Localização**: `Pies-front/src/main/java/com/pies/projeto/integrado/piesfront/dto/CreateTurmaDTO.java`
- **Função**: DTO para enviar dados de criação de turma ao backend
- **Campos**:
  - `nome`: Nome da turma
  - `grauEscolar`: Grau escolar (EDUCACAO_INFANTIL, FUNDAMENTAL_I, FUNDAMENTAL_II)
  - `faixaEtaria`: Faixa etária dos alunos
  - `turno`: Turno (MATUTINO, VESPERTINO)
  - `professorId`: ID do professor responsável

#### `ProfessorDTO.java`
- **Localização**: `Pies-front/src/main/java/com/pies/projeto/integrado/piesfront/dto/ProfessorDTO.java`
- **Função**: DTO para receber dados de professores da API
- **Usado para**: Popular o ChoiceBox de seleção de professores

### 2. Controller Atualizado

#### `CadastroTurmaController.java`
- **Localização**: `Pies-front/src/main/java/com/pies/projeto/integrado/piesfront/controllers/CadastroTurmaController.java`
- **Funcionalidades Implementadas**:

##### Inicialização
- `inicializarChoiceBoxes()`: Popula os ChoiceBoxes com valores dos enums do backend
  - Graus escolares: EDUCACAO_INFANTIL, FUNDAMENTAL_I, FUNDAMENTAL_II
  - Turnos: MATUTINO, VESPERTINO
  - Faixas etárias: 4-5 anos, 6-8 anos, 9-11 anos, 12-14 anos

- `carregarProfessores()`: 
  - Faz requisição GET para `/professores`
  - Carrega lista de professores disponíveis
  - Popula o ChoiceBox de professor responsável

##### Validação
- `validarFormulario()`: Valida todos os campos obrigatórios
  - Nome da turma
  - Grau escolar
  - Faixa etária
  - Professor responsável
  - Turno

##### Envio
- `enviarCadastroTurma()`:
  - Valida o formulário
  - Monta o DTO `CreateTurmaDTO`
  - Envia requisição POST para `/turmas`
  - Trata respostas de sucesso (200/201) e erro (400)
  - Retorna para tela inicial após sucesso

##### Auxiliares
- `obterProfessorIdSelecionado()`: Converte seleção do ChoiceBox em ID do professor
- `mostrarErro()` / `limparErro()`: Gerencia mensagens de erro na interface

### 3. FXML Atualizado

#### `cadastro-de-turma.fxml`
- **Localização**: `Pies-front/src/main/resources/com/pies/projeto/integrado/piesfront/screens/cadastro-de-turma.fxml`
- **Mudanças**:
  - Adicionado `TextField` `nomeTurmaField` para entrada do nome da turma
  - Adicionado `Label` `erroMensagem` para exibir mensagens de erro
  - Ajustados os margins dos ChoiceBoxes para melhor espaçamento
  - Configurado o label de título "Dados da Turma"

## Fluxo de Funcionamento

### 1. Carregamento da Tela
1. Controller inicializa
2. ChoiceBoxes são populados com valores fixos
3. Sistema carrega professores do backend via GET `/professores`
4. ChoiceBox de professores é populado com formato "Nome - CPF"

### 2. Preenchimento do Formulário
O usuário preenche:
- Nome da turma (TextField)
- Grau escolar (ChoiceBox)
- Faixa etária (ChoiceBox)
- Professor responsável (ChoiceBox)
- Turno (ChoiceBox)

### 3. Submissão
1. Usuário clica em "Cadastrar Turma"
2. Sistema valida todos os campos
3. Se válido, monta o DTO e envia POST para `/turmas`
4. Backend valida e salva a turma
5. Em caso de sucesso, retorna para tela inicial
6. Em caso de erro, exibe mensagem

### 4. Cancelamento
- Botão "Cancelar" retorna para tela inicial sem salvar

## Integração com Backend

### Endpoint Utilizado
- **POST** `/turmas`
- **Autenticação**: Bearer Token (JWT)
- **Permissões**: COORDENADOR ou ADMIN

### Formato do Request Body
```json
{
  "nome": "Turma A - Infantil",
  "grauEscolar": "EDUCACAO_INFANTIL",
  "faixaEtaria": "4-5 anos",
  "turno": "MATUTINO",
  "professorId": "uuid-do-professor"
}
```

### Respostas
- **200/201**: Turma criada com sucesso
- **400**: Dados inválidos ou professor não encontrado
- **401/403**: Não autorizado

## Padrão Seguido

A implementação segue o mesmo padrão do `CadastroProfController`:

1. **HttpClient** para requisições HTTP
2. **ObjectMapper** para serialização JSON
3. **AuthService** para gerenciamento de token
4. **Validação** no front-end antes de enviar
5. **Tratamento de erros** com mensagens ao usuário
6. **Debug logs** no console para troubleshooting
7. **Navegação** para tela inicial após sucesso

## Dependências

### Backend
- `TurmaController` implementado
- Enums `GrauEscolar` e `Turno` definidos
- `ProfessorController` para listar professores
- Sistema de autenticação JWT funcionando

### Frontend
- JavaFX para interface
- Jackson para JSON
- HttpClient Java 11+ para requisições

## Como Testar

1. **Inicie o backend** (porta 8080)
2. **Inicie o frontend**
3. **Faça login** como COORDENADOR ou ADMIN
4. **Navegue** para "Cadastro de Turma"
5. **Preencha** todos os campos:
   - Digite o nome da turma
   - Selecione o grau escolar
   - Selecione a faixa etária
   - Selecione um professor (deve haver professores cadastrados)
   - Selecione o turno
6. **Clique** em "Cadastrar Turma"
7. **Verifique** se retornou para tela inicial
8. **Confirme** no backend que a turma foi criada

## Possíveis Melhorias Futuras

1. **Adicionar alunos à turma** durante o cadastro (funcionalidade já presente no FXML mas não implementada)
2. **Validação de nome duplicado** no front-end
3. **Campo de pesquisa** de professores
4. **Feedback visual** durante o carregamento
5. **Cache** da lista de professores
6. **Edição de turmas** existentes
7. **Listagem de turmas** cadastradas

## Observações

- O sistema carrega automaticamente a lista de professores cadastrados
- É necessário ter pelo menos um professor cadastrado para criar uma turma
- Os valores dos enums devem corresponder exatamente aos definidos no backend
- O token JWT é gerenciado automaticamente pelo AuthService
- Mensagens de erro são exibidas em vermelho na interface

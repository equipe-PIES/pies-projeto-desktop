# Plano AEE - Sistema de Gestão APAPEQ

Sistema desktop Java desenvolvido para automatizar processos manuais de relatórios, frequência e planos de aula da APAPEQ (Associação de Pais e Amigos de Pessoas Especiais de Quixadá).

## 🎯 Objetivo

Automatizar e organizar os processos de gestão educacional para profissionais que atendem crianças neurodivergentes, incluindo:
- Controle de frequência diária
- Geração de relatórios semestrais
- Criação e gestão de planos de aula
- Gestão de usuários (profissionais)

## 🏗️ Arquitetura

O sistema segue o padrão MVC (Model-View-Controller) com as seguintes camadas:

```
📦 com.planoaee
├── 📂 view/ (JavaFX)
│   ├── controllers/ (Controllers FXML)
│   ├── fxml/ (Layouts das telas)
│   └── css/ (Estilos)
├── 📂 service/ (Regras de negócio)
├── 📂 repository/ (Acesso a dados)
├── 📂 database/ (Conexão SQLite)
├── 📂 model/ (Entidades)
└── 📂 util/ (Utilitários)
```

## 🛠️ Tecnologias

- **Java 17+** - Linguagem principal
- **JavaFX** - Interface gráfica desktop
- **SQLite** - Banco de dados local
- **Maven** - Gerenciamento de dependências
- **Jackson** - Processamento JSON
- **Apache POI** - Exportação Excel/Word
- **iText** - Geração de PDF

## 📋 Funcionalidades

### ✅ Implementado
- [x] Estrutura inicial do projeto Maven
- [x] Conexão e inicialização do banco SQLite
- [x] Modelos de dados (Usuario, Aluno, Frequencia, Relatorio, PlanoAula)
- [x] Sistema de login com autenticação
- [x] Interface de login moderna
- [x] Dashboard principal
- [x] Criptografia de senhas (SHA-256)

### 🚧 Em Desenvolvimento
- [ ] CRUD completo de alunos
- [ ] Controle de frequência diária
- [ ] Sistema de relatórios
- [ ] Planos de aula
- [ ] Configurações do sistema
- [ ] Sistema de backup
- [ ] Exportação de dados

## 👥 Tipos de Usuários

- **Professor** - Cria planos de aula e relatórios educacionais
- **Psicólogo** - Gera relatórios psicológicos
- **Psiquiatra** - Gera relatórios psiquiátricos
- **Fisioterapeuta** - Gera relatórios fisioterápicos

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Instalação
1. Clone o repositório
2. Navegue até o diretório do projeto
3. Execute o Maven para baixar dependências:
   ```bash
   mvn clean install
   ```

### Execução
```bash
# Executar via Maven
mvn javafx:run

# Ou compilar e executar JAR
mvn clean package
java -jar target/plano-aee-1.0.0.jar
```

## 🔐 Login Padrão

- **E-mail**: admin@apapeq.com
- **Senha**: admin123

## 📊 Banco de Dados

O sistema utiliza SQLite como banco de dados local (`plano_aee.db`), que é criado automaticamente na primeira execução com:

- Tabela de usuários
- Tabela de alunos
- Tabela de frequência
- Tabela de relatórios
- Tabela de planos de aula
- Tabela de configurações

## 📁 Estrutura de Arquivos

```
programação/
├── pom.xml                          # Configuração Maven
├── src/main/java/com/planoaee/
│   ├── MainApp.java                 # Classe principal
│   ├── database/
│   │   ├── DatabaseConnection.java  # Conexão SQLite
│   │   └── DatabaseInitializer.java # Inicialização do BD
│   ├── model/                       # Entidades do sistema
│   ├── repository/                  # Acesso a dados
│   ├── service/                     # Regras de negócio
│   ├── util/                        # Utilitários
│   └── view/
│       ├── controllers/             # Controllers JavaFX
│       ├── fxml/                    # Layouts das telas
│       └── css/                     # Estilos CSS
├── src/main/resources/
│   ├── logging.properties           # Configuração de logs
│   └── com/planoaee/view/
│       ├── fxml/                    # Arquivos FXML
│       └── css/                     # Arquivos CSS
└── README.md                        # Este arquivo
```

## 🎨 Interface

O sistema possui uma interface moderna e intuitiva com:
- Tela de splash/loading
- Login com validações
- Dashboard com cards informativos
- Design responsivo e acessível
- Tema personalizado da APAPEQ

## 📝 Logs

Os logs do sistema são salvos em:
- Console (durante desenvolvimento)
- Arquivo: `logs/plano-aee-*.log`

## 🤝 Contribuição

Este é um projeto desenvolvido especificamente para a APAPEQ. Para sugestões ou melhorias, entre em contato com a equipe de desenvolvimento.

## 📞 Suporte

Para suporte técnico ou dúvidas sobre o sistema:
- E-mail: admin@apapeq.com
- Sistema: Plano AEE v1.0.0

---

**APAPEQ - Associação de Pais e Amigos de Pessoas Especiais de Quixadá**  
*Transformando vidas através da educação especial*



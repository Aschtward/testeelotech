# Sistema de Gerenciamento de Biblioteca

Sistema fullstack para gerenciamento de uma biblioteca, permitindo o cadastro de **usuarios**, **livros** e **emprestimos**, com recomendacao de livros baseada em categorias e integracao com a API do Google Books.

## Sumario

- [Arquitetura Geral](#arquitetura-geral)
- [Tecnologias](#tecnologias)
- [Docker](#docker)
- [Backend](#backend)
  - [Estrutura de Pacotes](#estrutura-de-pacotes)
  - [Camada de Dominio](#camada-de-dominio)
  - [Camada de Servico](#camada-de-servico)
  - [Camada de API](#camada-de-api)
  - [Endpoints REST](#endpoints-rest)
  - [Infraestrutura](#infraestrutura)
- [Frontend](#frontend)
  - [Estrutura de Componentes](#estrutura-de-componentes)
  - [Componente Generico de Tabela](#componente-generico-de-tabela)
  - [Servicos](#servicos)
  - [Rotas](#rotas)
- [Decisoes de Projeto](#decisoes-de-projeto)

---

## Arquitetura Geral

O projeto segue uma arquitetura de tres camadas com separacao clara de responsabilidades:

```
┌──────────────────────────────────────────────┐
│            Frontend (Angular 19)             │
│        http://localhost:4200 (porta 80)      │
└───────────────────┬──────────────────────────┘
                    │ HTTP REST
┌───────────────────▼──────────────────────────┐
│          Backend (Spring Boot 4)             │
│            http://localhost:8080             │
└───────────────────┬──────────────────────────┘
                    │ JPA/Hibernate
┌───────────────────▼──────────────────────────┐
│              MySQL 8 Database                │
│            localhost:3306                     │
└──────────────────────────────────────────────┘
```

---

## Tecnologias

| Camada     | Tecnologia                                      |
|------------|------------------------------------------------|
| Frontend   | Angular 19, Angular Material 19, TailwindCSS 3, RxJS 7, TypeScript 5.7 |
| Backend    | Java 21, Spring Boot 4.0.4, Spring Data JPA, Lombok |
| Banco      | MySQL 8                                         |
| Build      | Maven (backend), Angular CLI (frontend)         |
| Docker     | Docker Compose, Nginx (frontend), Temurin JDK 21 |
| Docs       | SpringDoc OpenAPI 3 (Swagger UI)               |

---

## Docker

O projeto inteiro e orquestrado via Docker Compose com tres servicos:

### Servicos

| Servico    | Imagem Base                     | Porta  | Descricao                          |
|------------|--------------------------------|--------|-------------------------------------|
| `mysql`    | `mysql:8`                      | 3306   | Banco de dados MySQL                |
| `backend`  | Multi-stage: Maven + Temurin 21 | 8080   | API REST Spring Boot               |
| `frontend` | Multi-stage: Node 18 + Nginx   | 4200→80 | SPA Angular servida via Nginx      |

### Dockerfiles

**Backend** (`backend/Dockerfile`): Build multi-stage - compila com Maven, executa com Eclipse Temurin JDK 21.

**Frontend** (`frontend/Dockerfile`): Build multi-stage - compila com Node 18 (`npm run build`), serve arquivos estaticos com Nginx Alpine.

### Variaveis de Ambiente (`.env`)

| Variavel              | Descricao                    |
|-----------------------|-----------------------------|
| `MYSQL_ROOT_PASSWORD` | Senha root do MySQL          |
| `MYSQL_DATABASE`      | Nome do banco de dados       |
| `GOOGLE_BOOKS_KEY`    | Chave da API Google Books    |

### Comandos

**Windows:**
```bash
dev.bat up      # Subir todos os servicos
dev.bat down    # Parar todos os servicos
dev.bat logs    # Ver logs
```

**Linux/Mac:**
```bash
./scripts/start.sh   # Subir todos os servicos
./scripts/stop.sh    # Parar todos os servicos
```

### Configuracao

Copie `exemplo.env` para `.env` e preencha a chave do Google Books:

```bash
cp exemplo.env .env
```

### Dump do Banco

O arquivo `dump.sql` na raiz contem um dump MySQL com dados de exemplo (6 usuarios, 14 livros, 5 emprestimos) que pode ser importado para popular o banco inicial.

---

## Backend

### Estrutura de Pacotes

```
com.elotech.teste
├── api
│   ├── controller
│   │   ├── emprestimo/EmprestimoController
│   │   ├── livro/LivroController
│   │   └── usuario/UsuarioController
│   └── dto
│       ├── emprestimo/EmprestimoDTO
│       ├── livro/
│       │   ├── LivroDTO
│       │   ├── VolumeDTO
│       │   ├── GoogleBooksResponseDTO
│       │   ├── GoogleBooksItensDTO
│       │   └── IndustryIdentifierDTO
│       └── usuario/UsuarioDTO
├── domain
│   ├── entity
│   │   ├── abstractentity/AbstractEntity
│   │   ├── emprestimo/Emprestimo, EmprestimoStatus
│   │   ├── livro/Livro
│   │   └── usuario/Usuario
│   ├── valueobject/
│   │   ├── VoData
│   │   ├── VoEmail
│   │   └── VoTelefone
│   └── converters/
│       ├── VoDataConverter
│       ├── VoEmailConverter
│       └── VoTelefoneConverter
├── infra
│   ├── CorsConfiguration
│   ├── config/
│   │   ├── JacksonConfig
│   │   ├── GoogleBooksConfig
│   │   └── GoogleBooksRestClientConfig
│   └── client/GoogleBooksRestClient
├── repository
│   ├── emprestimo/EmprestimoRepository
│   ├── livro/LivroRepository
│   └── usuario/UsuarioRepository
├── service
│   ├── emprestimo/EmprestimoService
│   ├── livro/LivroService
│   ├── usuario/UsuarioService
│   └── googlebooks/GoogleBooksService
└── TesteApplication
```

### Camada de Dominio

#### Entidades

Todas herdam de `AbstractEntity`, que fornece `id` (auto-gerado, `GenerationType.IDENTITY`).

**`Usuario`**
| Campo          | Tipo        | Coluna           | Descricao                          |
|----------------|-------------|------------------|------------------------------------|
| `nome`         | String      | `nome`           | Nome do usuario                    |
| `email`        | VoEmail     | `email`          | Email validado por regex           |
| `dataCadastro` | VoData      | `data_cadastro`  | Data de cadastro                   |
| `telefone`     | VoTelefone  | `telefone`       | Telefone brasileiro (+55)          |

**`Livro`** (implementa `Comparable<Livro>`)
| Campo            | Tipo          | Coluna            | Descricao                        |
|------------------|---------------|-------------------|----------------------------------|
| `titulo`         | String        | `titulo`          | Titulo do livro                  |
| `autor`          | String        | `autor`           | Autor do livro                   |
| `isbn`           | String        | `isbn`            | Codigo ISBN                      |
| `dataPublicacao` | VoData        | `data_publicacao` | Data de publicacao               |
| `categorias`     | List\<String> | `@ElementCollection` | Lista de categorias           |

**`Emprestimo`**
| Campo            | Tipo             | Coluna            | Descricao                        |
|------------------|------------------|-------------------|----------------------------------|
| `usuario`        | Usuario          | `usuario_id` (FK) | Relacao ManyToOne                |
| `livro`          | Livro            | `livro_id` (FK)   | Relacao ManyToOne                |
| `status`         | EmprestimoStatus | `status`          | Status calculado automaticamente |
| `dataEmprestimo` | VoData           | `data_emprestimo` | Data do emprestimo               |
| `dataVencimento` | VoData           | `data_vencimento` | Data de vencimento (nullable)    |
| `dataDevolucao`  | VoData           | `data_devolucao`  | Data de devolucao (nullable)     |

**`EmprestimoStatus`** (Enum)
| Valor              | Codigo | Descricao                     |
|--------------------|--------|-------------------------------|
| `ABERTO_EM_DIA`    | AD     | Em aberto, dentro do prazo    |
| `ABERTO_ATRASADO`  | AT     | Em aberto, fora do prazo      |
| `FECHADO_EM_DIA`   | FD     | Fechado, dentro do prazo      |
| `FECHADO_ATRASADO` | FT     | Fechado, fora do prazo        |

O status e calculado automaticamente no metodo `definirStatus()` com base nas datas de vencimento e devolucao.

#### Value Objects

O projeto utiliza o padrao **Value Object** (DDD) para encapsular validacao e logica de negocios:

- **`VoData`**: Encapsula `ZonedDateTime`. Metodos utilitarios: `hoje()`, `maisDias()`, `menosDias()`, `isAntesDe()`, `isDepoisDe()`. Implementa `Comparable<VoData>`.
- **`VoEmail`**: Valida formato de email via regex `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`. Lanca `IllegalArgumentException` se invalido.
- **`VoTelefone`**: Normaliza numeros de 11 digitos para formato `+55...` e valida o padrao `^\+55\d{11}$`.

Cada Value Object possui um `@Converter(autoApply = true)` correspondente para persistencia automatica via JPA, alem de serem `@Embeddable` utilizados com `@Embedded` + `@AttributeOverride` nas entidades.

### Camada de Servico

Todos os servicos sao `@Transactional` com injecao de dependencia via construtor (`@RequiredArgsConstructor`).

- **`UsuarioService`**: CRUD de usuarios.
- **`LivroService`**: CRUD de livros + integracao com Google Books para busca e recomendacao.
- **`EmprestimoService`**: CRUD de emprestimos com regras de negocio:
  - Impede emprestimo duplicado do mesmo livro (verifica se ja existe emprestimo ativo).
  - Calcula `dataVencimento` automaticamente a partir de `periodoVencimento` quando nao informada.
  - Atualiza status em lote (devolucao de multiplos emprestimos via PATCH).
- **`GoogleBooksService`**: Integra com a API Google Books usando `RestClient`, retornando livros convertidos para o formato interno.

### Camada de API

#### DTOs

Os DTOs sao implementados como **Java Records** para imutabilidade:

- `UsuarioDTO(id, nome, email, dataCadastro, telefone)`
- `LivroDTO(id, titulo, autor, isbn, dataPublicacao, categorias)`
- `EmprestimoDTO(id, nomeUsuario, tituloLivro, dataEmprestimo, dataVencimento, dataDevolucao, periodoVencimento, status, livro, usuario)`

DTOs do Google Books: `GoogleBooksResponseDTO`, `GoogleBooksItensDTO`, `VolumeDTO`, `IndustryIdentifierDTO` — mapeiam a resposta da API do Google Books para o formato interno `LivroDTO`.

### Endpoints REST

#### Usuarios (`/usuarios`)

| Metodo | Rota                | Descricao                                      |
|--------|---------------------|-------------------------------------------------|
| GET    | `/usuarios`         | Lista todos os usuarios                         |
| GET    | `/usuarios/{nome}`  | Busca usuarios por nome (case-insensitive)      |
| POST   | `/usuarios`         | Cria um novo usuario                            |
| PUT    | `/usuarios`         | Atualiza um usuario                             |
| DELETE | `/usuarios/{id}`    | Remove um usuario                               |

#### Livros (`/livros`)

| Metodo | Rota                                  | Descricao                                          |
|--------|---------------------------------------|----------------------------------------------------|
| GET    | `/livros`                             | Lista todos os livros                              |
| GET    | `/livros/{titulo}`                    | Busca livros por titulo (case-insensitive)         |
| GET    | `/livros/recomendar/{idUsuario}`      | Recomenda livros para o usuario baseado em categorias |
| GET    | `/livros/google/{consulta}/{pagina}`  | Busca livros na API Google Books                   |
| POST   | `/livros`                             | Cria um novo livro                                 |
| PUT    | `/livros`                             | Atualiza um livro                                  |
| DELETE | `/livros/{id}`                        | Remove um livro                                    |

#### Emprestimos (`/emprestimos`)

| Metodo | Rota                         | Descricao                                        |
|--------|------------------------------|-------------------------------------------------|
| GET    | `/emprestimos`               | Lista todos os emprestimos                       |
| GET    | `/emprestimos/{nomeUsuario}` | Busca emprestimos por nome do usuario            |
| POST   | `/emprestimos`               | Cria um novo emprestimo                          |
| PUT    | `/emprestimos`               | Atualiza um emprestimo                           |
| DELETE | `/emprestimos/{id}`          | Remove um emprestimo                             |
| PATCH  | `/emprestimos`               | Devolve multiplos emprestimos (recebe lista de IDs) |

### Infraestrutura

- **CORS**: Permite origens `localhost:4200` e `localhost:8080`, metodos GET/POST/PUT/DELETE/PATCH.
- **Jackson**: `ObjectMapper` customizado com `findAndRegisterModules()` para suporte a `ZonedDateTime` (JSR310).
- **Google Books**: Configuracao via `application.properties` com `RestClient` do Spring 6.
- **Swagger**: Disponivel via SpringDoc OpenAPI em `/swagger-ui.html`.

### Repositorios

- **`LivroRepository`**: Query JPQL customizada `listarRecomendados` que busca livros com categorias semelhantes aos ja emprestados pelo usuario, excluindo livros ja lidos e com emprestimos ativos.
- **`EmprestimoRepository`**: Queries JPQL para listar emprestimos com eager loading (`JOIN`) e buscar por nome de usuario.
- **`UsuarioRepository`**: Busca por nome com `findByNomeContainingIgnoreCase`.

---

## Frontend

### Estrutura de Componentes

```
src/app/
├── app.component                     # Shell principal (sidenav + toolbar + router-outlet)
├── app.routes.ts                     # Configuracao de rotas
├── app.config.ts                     # Configuracao global (locale pt-BR, date adapter)
├── tabela-component/                 # Componente generico de tabela CRUD
│   ├── tabela-component.component    # Logica da tabela
│   └── BasicCrudService.ts           # Interface generica CrudService<T>
├── usuario/
│   ├── usuario.component             # Pagina de usuarios (usa tabela-component)
│   ├── usuario.service               # Servico HTTP para usuarios
│   └── usuario-dialog/               # Dialog de criacao/edicao de usuario
├── livro/
│   ├── livro.component               # Pagina de livros (usa tabela-component)
│   ├── livro.service                  # Servico HTTP para livros
│   └── livro-dialog/                  # Dialog com autocomplete Google Books
└── emprestimo/
    ├── emprestimo.component           # Pagina de emprestimos (usa tabela-component)
    ├── emprestimo.service             # Servico HTTP para emprestimos
    ├── emprestimo-dialog/             # Dialog de criacao/edicao de emprestimo
    └── emprestimo-recomendacao-dialog/ # Dialog de emprestimo por recomendacao
```

### Componente Generico de Tabela

O `TabelaComponentComponent<T extends Entity>` e o componente central do frontend, eliminando duplicacao de codigo CRUD entre as paginas.

**Inputs:**

| Input               | Tipo                   | Descricao                                     |
|---------------------|------------------------|-----------------------------------------------|
| `displayedColumns`  | `string[]`             | Colunas a exibir (chaves do objeto)           |
| `displayedLabels`   | `string[]`             | Labels das colunas                            |
| `service`           | `CrudService<T>`       | Servico que implementa as operacoes CRUD      |
| `dialogComponent`   | `ComponentType<any>`   | Componente do dialog de criacao/edicao        |
| `actions`           | `Actions[]`            | Acoes em lote (ex: Devolver, Recomendar)      |
| `emptyItem`         | `() => T`              | Factory para novo item vazio                  |

**Output:**

| Output              | Tipo                   | Descricao                                     |
|---------------------|------------------------|-----------------------------------------------|
| `selectionChange`   | `EventEmitter<T[]>`    | Emite itens selecionados                      |

**Funcionalidades:**
- Busca com debounce (300ms) via `Subject` + `switchMap`
- CRUD completo via dialogs modais (MatDialog)
- Selecao multipla com checkboxes (`SelectionModel`) quando existem acoes
- Acoes em lote (menu dropdown com callbacks que retornam `Observable`)
- Spinner de carregamento
- Mensagem de "Nenhum registro encontrado" quando vazio
- Deteccao automatica de datas para formatacao com `DatePipe` (`dd/MM/yyyy`)

**Interface `CrudService<T>`:**

```typescript
interface CrudService<T extends Entity> {
  list(): Observable<T[]>;
  find(query: string): Observable<T[]>;
  save(valor: T): Observable<T>;
  update(valor: T): Observable<T>;
  delete(id: number): Observable<void>;
}
```

### Servicos

| Servico              | API Base                     | Metodos Adicionais                              |
|----------------------|------------------------------|------------------------------------------------|
| `UsuarioService`     | `/usuarios`                  | -                                               |
| `LivroService`       | `/livros`                    | `listFromGoogleBooks(query)`, `listFromRecomendacao(usuarioId)` |
| `EmprestimoService`  | `/emprestimos`               | `devolver(ids: number[])`                       |

### Dialogs

- **`UsuarioDialogComponent`**: Formulario com campos nome, email, telefone e dataCadastro (MatDatepicker). Validacao via `NgForm`.
- **`LivroDialogComponent`**: Formulario com autocomplete para buscar livros no Google Books (debounced). Campo de categorias com `MatChipGrid` (adicionar/remover tags). Ao selecionar um livro do Google, preenche titulo, autor, ISBN, data e categorias automaticamente.
- **`EmprestimoDialogComponent`**: Dois autocompletes (usuario e livro) com busca debounced. Tres datepickers com refs unicos (`pickerEmprestimo`, `pickerVencimento`, `pickerDevolucao`). Campo `periodoVencimento` numerico.
- **`EmprestimoRecomendacaoDialogComponent`**: Ao selecionar um usuario, busca livros recomendados (baseados em categorias) e permite selecionar um para emprestimo.

### Rotas

| Rota           | Componente           |
|----------------|---------------------|
| `/usuario`     | `UsuarioComponent`   |
| `/livro`       | `LivroComponent`     |
| `/emprestimo`  | `EmprestimoComponent`|

### Configuracao Global (`app.config.ts`)

- Locale `pt-BR` registrado para formatacao de datas e numeros
- `provideNativeDateAdapter()` para MatDatepicker
- `MAT_DATE_LOCALE: 'pt-BR'` para datepickers em portugues

---

## Decisoes de Projeto

### Backend

1. **Value Objects (DDD)**: Campos como email, telefone e data sao encapsulados em Value Objects (`VoEmail`, `VoTelefone`, `VoData`) que validam no momento da criacao, garantindo consistencia no dominio. Cada VO possui um `@Converter(autoApply = true)` para persistencia transparente.

2. **Entidades com construtores protegidos**: Uso do padrao factory (`Entidade.of(...)`) para criacao, evitando instanciacao invalida. `NoArgsConstructor(PROTECTED)` atende ao requisito do JPA sem expor construtor vazio.

3. **Status calculado automaticamente**: `EmprestimoStatus` e derivado das datas (emprestimo, vencimento, devolucao) no metodo `definirStatus()`, eliminando inconsistencias manuais.

4. **Recomendacao por categorias**: Query JPQL no `LivroRepository` que encontra livros com categorias em comum com os ja emprestados pelo usuario, excluindo livros ja lidos e com emprestimos ativos.

5. **DTOs como Records**: Imutabilidade e concisao com Java Records para todos os DTOs.

6. **Google Books como REST Client**: Uso do `RestClient` do Spring 6 (nao RestTemplate) com configuracao externalizada via `@ConfigurationProperties`.

### Frontend

1. **Componente generico de tabela**: `TabelaComponentComponent<T>` elimina duplicacao de CRUD entre as tres paginas, aceitando qualquer servico que implemente `CrudService<T>` e qualquer dialog component.

2. **Standalone components**: Todos os componentes sao standalone (Angular 19), sem `NgModule`, com imports declarados diretamente no `@Component`.

3. **Busca com debounce**: Todas as buscas (tabela, autocomplete de livros, autocomplete de usuarios) utilizam `Subject` + `debounceTime(300)` + `distinctUntilChanged` + `switchMap` para evitar requisicoes desnecessarias.

4. **Locale brasileiro**: Datas exibidas em formato `dd/MM/yyyy`, datepickers em portugues, validacao de telefone no padrao `+55`.

5. **Angular Material**: Design system completo com MatTable, MatDialog, MatAutocomplete, MatDatepicker, MatChipGrid, MatSidenav, MatSnackBar, MatProgressSpinner.

6. **Acoes em lote**: O componente de tabela suporta selecao multipla com checkboxes e acoes em lote (como "Devolver" para emprestimos), onde cada acao e um callback que retorna `Observable` para controle de fluxo.

### Infraestrutura

1. **Docker Compose**: Orquestracao simples com `dev.bat` para subir/parar o ambiente completo.
2. **Multi-stage builds**: Tanto backend quanto frontend usam builds multi-stage para imagens Docker otimizadas (build separado da execucao).
3. **DDL auto-update**: `spring.jpa.hibernate.ddl-auto=update` gera/atualiza tabelas automaticamente a partir das entidades JPA.

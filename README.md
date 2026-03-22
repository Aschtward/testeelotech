# Sistema de Gerenciamento de Biblioteca

Sistema fullstack para gerenciamento de uma biblioteca, permitindo o cadastro de **usuarios**, **livros** e **emprestimos**, com recomendacao de livros baseada em categorias e integracao com a API do Google Books.
Esse projeto foi desenvolvido como parte de um desafio tecnico proposto pela Elotech.

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

A ideia do projeto foi manter as responsabilidades claras e evitar que alguma camada tenha conhecimento
indevido de outra. Para isso foi adotado a seguinte arquitetura:

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

O projeto roda no docker sendo orquestrado com servicos para o backend, frontend e o banco de dados

### Servicos

| Servico    | Imagem Base                     | Porta  | Descricao                          |
|------------|--------------------------------|--------|-------------------------------------|
| `mysql`    | `mysql:8`                      | 3306   | Banco de dados MySQL                |
| `backend`  | Multi-stage: Maven + Temurin 21 | 8080   | API REST Spring Boot               |
| `frontend` | Multi-stage: Node 18 + Nginx   | 4200→80 | SPA Angular servida via Nginx      |

### Dockerfiles

**Backend** (`backend/Dockerfile`): Arquivo Dockerfile para o backend.

**Frontend** (`frontend/Dockerfile`): Arquivo Dockerfile para o frontend.

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

Para rodar o projeto e necessario configurar o `.env` para isso foi deixado de exemplo um arquivo
`exemplo.env`, a unica configuracao relevante nesse arquivo seria a atualizacao da API key do Google Books

É importante ressaltar que e necessario criar o arquivo `.env`, visto que ele nao e incluso no projeto por questoes de seguranca

### Dump do Banco

Para facilitar a visualizacao do projeto e a realizacao de testes foi criado um dump do banco de 
dados, esse dump contem informacoes sobre usuarios, livros e emprestimos.

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

No escopo inicial do projeto foi definido a criacao de uma `AbstractEntity` que forneceria
campos que sao utilizados em todas as entidades no sistema, nesse caso foi utilizado para o `id`,
com a ideia de expandir para outros casos principalmente considerando versionamento.

As entidades utilizadas foram criadas da seguinte forma:

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

O status e calculado automaticamente no metodo `definirStatus()` com base nas datas de vencimento e devolucao. O status tambem pode ser inferido se o usuario passar um periodo vencimento, isso calcula
automaticamente a data de vencimento e o status do emprestimo

#### Value Objects

Com o objeto de centralizar o tratamento de alguns dados e garantir a imutabilidade do sistema foi definido
o uso do padrao **Value Object** para os seguintes tipos:

- **`VoData`**: Encapsula `ZonedDateTime`. Metodos utilitarios: `hoje()`, `maisDias()`, `menosDias()`, `isAntesDe()`, `isDepoisDe()`. Implementa `Comparable<VoData>`.
- **`VoEmail`**: Valida formato de email via regex `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`. Lanca `IllegalArgumentException` se invalido.
- **`VoTelefone`**: Normaliza numeros de 11 digitos para formato `+55...` e valida o padrao `^\+55\d{11}$`.

Cada Value Object possui um `@Converter(autoApply = true)` para ser aplicado ao persistir dados.

### Camada de Servico

Todos os servicos sao `@Transactional` com injecao de dependencia via construtor (`@RequiredArgsConstructor`).

- **`UsuarioService`**: CRUD de usuarios.
- **`LivroService`**: CRUD de livros e integracao com Google Books para busca e recomendacao.
- **`EmprestimoService`**: CRUD de emprestimos com regras de negocio:
  - Impede emprestimo duplicado do mesmo livro verificando se ja existe emprestimo ativo.
  - Calcula `dataVencimento` a partir de `periodoVencimento` quando nao informada.
  - Atualiza status em grupo
- **`GoogleBooksService`**: Integra com a API Google Books usando `RestClient`, retornando livros convertidos para o formato interno.

### Camada de API

#### DTOs

Os DTOs sao implementados com **Java Records**:

- `UsuarioDTO(id, nome, email, dataCadastro, telefone)`
- `LivroDTO(id, titulo, autor, isbn, dataPublicacao, categorias)`
- `EmprestimoDTO(id, nomeUsuario, tituloLivro, dataEmprestimo, dataVencimento, dataDevolucao, periodoVencimento, status, livro, usuario)`

DTOs do Google Books: `GoogleBooksResponseDTO`, `GoogleBooksItensDTO`, `VolumeDTO`, `IndustryIdentifierDTO`.

### Endpoints REST

#### Usuarios (`/usuarios`)

| Metodo | Rota                | Descricao                                      |
|--------|---------------------|-------------------------------------------------|
| GET    | `/usuarios`         | Lista todos os usuarios                         |
| GET    | `/usuarios/{nome}`  | Busca usuarios por nome                         |
| POST   | `/usuarios`         | Cria um novo usuario                            |
| PUT    | `/usuarios`         | Atualiza um usuario                             |
| DELETE | `/usuarios/{id}`    | Remove um usuario                               |

#### Livros (`/livros`)

| Metodo | Rota                                  | Descricao                                          |
|--------|---------------------------------------|----------------------------------------------------|
| GET    | `/livros`                             | Lista todos os livros                              |
| GET    | `/livros/{titulo}`                    | Busca livros por titulo                            |
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

- **CORS**: Configurado para permitir requisicoes internas e do frontend.
- **Jackson**: `ObjectMapper` para converter dados nos testes.
- **Google Books**: Configuracao via `application.properties` com `RestClient` para fazer requisicoes a API do Google Books.

### Repositorios

- **`LivroRepository`**: Query customizada `listarRecomendados` para buscar livros com categorias que o usuario tenha realizado emprestimo e que nao estejam em um empresitmo aberto.
- **`EmprestimoRepository`**: Queries para listar emprestimos e buscar por nome de usuario.
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

O `TabelaComponentComponent<T extends Entity>` e o componente principal, ele foi criado com o objetivo de facilitar e agilizar o desenvolvimento. Esse componente e responsavel pelas acoes basicas do CRUD, utilizando uma interface para os services.

**Inputs:**

| Input               | Tipo                   | Descricao                                     |
|---------------------|------------------------|-----------------------------------------------|
| `displayedColumns`  | `string[]`             | Colunas a exibir                              |
| `displayedLabels`   | `string[]`             | Labels das colunas                            |
| `service`           | `CrudService<T>`       | Servico que implementa as operacoes CRUD      |
| `dialogComponent`   | `ComponentType<any>`   | Componente do dialog de criacao/edicao        |
| `actions`           | `Actions[]`            | Acoes                                         |
| `emptyItem`         | `() => T`              | Factory para novo item vazio                  |

**Output:**

| Output              | Tipo                   | Descricao                                     |
|---------------------|------------------------|-----------------------------------------------|
| `selectionChange`   | `EventEmitter<T[]>`    | Emite itens selecionados                      |

**Funcionalidades:**
- Busca com debounce via `Subject` + `switchMap`
- CRUD completo via dialogs modais passadas pelo componente
- Selecao multipla com checkboxes para acoes
- Acoes aplicadas em grupo
- Spinner de carregamento
- Deteccao automatica de datas para formatacao com `DatePipe` (`dd/MM/yyyy`)

**Interface `CrudService<T>`:**

O `CrudService<T>` e uma interface que define como a implementacao de um service de CRUD deveria ser para ser utilizado no `TabelaComponentComponent<T extends Entity>`

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
- **`EmprestimoRecomendacaoDialogComponent`**: Formulario para recomendar livros ao selecionar um usuario, busca livros recomendados e permite selecionar um para emprestimo.

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

2. **Entidades com construtores protegidos**: Uso do padrao factory (`Entidade.of(...)`) para criacao.
Protegendo as entidades ao nao permitir o acesso aos seus construtores.

3. **Status calculado automaticamente**: `EmprestimoStatus` e derivado das datas de emprestimo, vencimento e devolucao, calculado automaticamente no `definirStatus()`, eliminando inconsistencias manuais.

4. **Recomendacao por categorias**: Query JPQL no `LivroRepository` que encontra livros com categorias em comum com os ja emprestados pelo usuario, excluindo livros ja lidos e com emprestimos ativos.

5. **DTOs como Records**: Imutabilidade com Java Records para todos os DTOs.

6. **Google Books como REST Client**: Uso do `RestClient` do Spring 6 com configuracao externalizada via `@ConfigurationProperties`.

### Frontend

1. **Componente generico de tabela**: `TabelaComponentComponent<T>` elimina duplicacao de CRUD entre as tres paginas, aceitando qualquer servico que implemente `CrudService<T>` e qualquer dialog component.

2. **Standalone components**: Todos os componentes sao standalone.

3. **Busca com debounce**: Todas as buscas (tabela, autocomplete de livros, autocomplete de usuarios) utilizam `Subject` + `debounceTime(300)` + `distinctUntilChanged` + `switchMap` para nao realizar requisicoes desnecessarias e esperar o input do usuario.

4. **Angular Material**: Foi utilizado o angular material para facilitar o desenvolvimento do frontend.

6. **Acoes em grupo**: O componente de tabela suporta selecao multipla com checkboxes e acoes em grupo,
essas acoes tem como objetivo facilitar o uso, posteriormente poderiam ser implementadas acoes como excluir para todas as telas.

### Infraestrutura

1. **Docker Compose**: Orquestracao simples para subir o ambiente completo.
2. **Multi-stage builds**: Tanto backend quanto frontend usam builds multi-stage para imagens Docker.
3. **DDL auto-update**: `spring.jpa.hibernate.ddl-auto=update` gera/atualiza tabelas automaticamente a partir das entidades JPA.

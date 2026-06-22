# API PNAE

API REST para gestão de alimentação escolar do **Programa Nacional de Alimentação Escolar (PNAE)**.

## Funcionalidades

- **Autenticação JWT** com controle de acesso por perfil (ADMIN, NUTRICIONISTA, DIRETOR, CONSULTOR)
- **Gestão de escolas e alunos** com cálculo automático de faixa etária
- **Cadastro de alimentos** com informações nutricionais completas
- **Cardápios semanais** com validação nutricional conforme requisitos PNAE por faixa etária
- **Controle de estoque** com entrada, saída, perda e alertas de vencimento
- **Cadastro de fornecedores** com dados de endereço
- **Relatórios gerenciais**: escolas, nutricional, estoque, consumo mensal e custo por aluno
- **Documentação interativa** via Swagger UI

## Endpoints

| Módulo         | Método | Endpoint                                      | Permissão                          |
|----------------|--------|-----------------------------------------------|------------------------------------|
| Auth           | POST   | `/api/auth/login`                             | Público                            |
| Auth           | POST   | `/api/auth/registrar`                         | ADMIN                              |
| Auth           | GET    | `/api/auth/perfil`                            | Autenticado                        |
| Escolas        | GET    | `/api/escolas`                                | Autenticado                        |
| Escolas        | POST   | `/api/escolas`                                | Autenticado                        |
| Escolas        | GET    | `/api/escolas/{id}`                           | Autenticado                        |
| Escolas        | PUT    | `/api/escolas/{id}`                           | Autenticado                        |
| Escolas        | DELETE | `/api/escolas/{id}`                           | ADMIN                              |
| Alunos         | GET    | `/api/alunos`                                 | Autenticado                        |
| Alunos         | POST   | `/api/alunos`                                 | Autenticado                        |
| Alunos         | DELETE | `/api/alunos/{id}`                            | ADMIN                              |
| Alimentos      | GET    | `/api/alimentos`                              | Autenticado                        |
| Alimentos      | POST   | `/api/alimentos`                              | ADMIN, NUTRICIONISTA               |
| Cardápios      | GET    | `/api/cardapios/{id}`                         | Autenticado                        |
| Cardápios      | POST   | `/api/cardapios`                              | ADMIN, NUTRICIONISTA               |
| Cardápios      | POST   | `/api/cardapios/{id}/itens`                   | ADMIN, NUTRICIONISTA               |
| Cardápios      | POST   | `/api/cardapios/{id}/validar`                 | ADMIN, NUTRICIONISTA               |
| Cardápios      | PATCH  | `/api/cardapios/{id}/aprovar`                 | ADMIN, DIRETOR                     |
| Cardápios      | PATCH  | `/api/cardapios/{id}/rejeitar`                | ADMIN, DIRETOR                     |
| Estoque        | POST   | `/api/estoque/entrada`                        | ADMIN, NUTRICIONISTA               |
| Estoque        | POST   | `/api/estoque/{id}/saida`                     | ADMIN, NUTRICIONISTA               |
| Estoque        | GET    | `/api/estoque/escola/{id}`                    | Autenticado                        |
| Fornecedores   | GET    | `/api/fornecedores`                           | ADMIN                              |
| Fornecedores   | POST   | `/api/fornecedores`                           | ADMIN                              |
| Relatórios     | GET    | `/api/relatorios/escolas`                     | ADMIN, DIRETOR, NUTRICIONISTA      |
| Relatórios     | GET    | `/api/relatorios/nutricional/cardapio/{id}`   | ADMIN, DIRETOR, NUTRICIONISTA      |
| Relatórios     | GET    | `/api/relatorios/estoque/escola/{id}`         | ADMIN, DIRETOR, NUTRICIONISTA      |
| Relatórios     | GET    | `/api/relatorios/consumo/escola/{id}`         | ADMIN, DIRETOR, NUTRICIONISTA      |
| Relatórios     | GET    | `/api/relatorios/custo-aluno/escola/{id}`     | ADMIN, DIRETOR, NUTRICIONISTA      |

## Como rodar

### Pré-requisitos

- Java 17+
- Docker Desktop

### 1. Subir o banco SQL Server

```bash
docker compose up -d
```

### 2. Iniciar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### 3. Documentação Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Para autenticar no Swagger:
1. Use `POST /api/auth/login` para obter o token JWT
2. Clique em **Authorize** (canto superior direito)
3. Informe `Bearer <token>` no campo `bearerAuth`

## Credenciais padrão

```
Email: admin@pnae.gov.br
Senha: Admin@2024!
Role:  ADMIN
```

> O usuário admin é criado automaticamente na primeira inicialização da aplicação.

## Arquitetura

Projeto organizado em camadas seguindo princípios de **Domain-Driven Design (DDD)**:

```
src/main/java/com/pnae/
├── api/
│   ├── controller/       # Controllers REST (@RestController)
│   └── exception/        # GlobalExceptionHandler + ErrorResponse
├── application/
│   ├── dto/              # Records de entrada/saída (Request/Response DTOs)
│   └── service/          # Application Services (orquestração)
├── domain/
│   ├── exception/        # Exceções de domínio (BusinessException, ResourceNotFoundException)
│   ├── model/            # Entidades JPA e Enums
│   ├── repository/       # Interfaces JpaRepository
│   └── service/          # Domain Services (ValidacaoNutricionalService)
└── infrastructure/
    └── config/           # Configurações (Security, JWT, Swagger, DataInitializer)
```

## Tecnologias

| Tecnologia              | Versão   |
|-------------------------|----------|
| Java                    | 17+      |
| Spring Boot             | 3.3.5    |
| Spring Security         | 6.x      |
| JJWT                    | 0.12.6   |
| Spring Data JPA         | 3.x      |
| SQL Server              | 2022     |
| Hibernate               | 6.x      |
| Lombok                  | 1.18.38  |
| springdoc-openapi       | 2.6.0    |
| Maven Wrapper           | 3.9.6    |
| Docker Compose          | —        |

## Banco de Dados

SQL Server 2022 via Docker Compose:

| Parâmetro | Valor        |
|-----------|--------------|
| Host      | localhost    |
| Porta     | 1433         |
| Banco     | pnae_db      |
| Usuário   | sa           |
| Senha     | Pnae@2024!   |

## Testes

```bash
./mvnw test
```

81 testes unitários e de integração cobrindo services, controllers e modelos de domínio.

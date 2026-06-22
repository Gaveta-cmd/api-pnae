# API PNAE

API REST para gestão de alimentação escolar do Programa Nacional de Alimentação Escolar (PNAE).

## Tecnologias

- Java 17
- Spring Boot 3.3
- SQL Server 2022
- Docker & Docker Compose
- Maven

## Arquitetura

Projeto organizado em camadas seguindo princípios de DDD:

```
src/main/java/com/pnae/
├── domain/          # Entidades, repositórios e regras de negócio
├── application/     # DTOs e serviços de aplicação
├── infrastructure/  # Configurações e persistência
└── api/             # Controllers REST
```

## Como rodar

```bash
# Subir SQL Server
docker compose up -d

# Rodar a aplicação
./mvnw spring-boot:run

# Health check
curl http://localhost:8080/health
```

## Funcionalidades

- Gestão de escolas e alunos
- Montagem de cardápios semanais
- Cálculo nutricional por faixa etária
- Controle de estoque de alimentos
- Relatórios de conformidade PNAE

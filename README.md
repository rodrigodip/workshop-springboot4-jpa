# Web Services with Spring Boot and JPA / Hibernate

> Learning project from Prof. Dr. Nelio Alves' course (devsuperior.com.br), extended beyond the syllabus.
> **Live demo:** https://java-springboot.rodrigodip.com.br

**Course goals (completed)**

- Create a Spring Boot Java project
- Implement the domain model
- Structure logical layers: resource, service, repository
- Configure a test database (H2)
- Seed the database
- CRUD - Create, Retrieve, Update, Delete
- Exception handling

## What this API does

REST API managing Users, Products, Categories and Orders (with items, status flow and payments).

| Resource | Endpoints |
|---|---|
| Users | `GET /users`, `GET /users/{id}`, `POST`, `PUT`, `DELETE` |
| Products | `GET /products`, `GET /products/{id}`, `POST`, `PUT`, `DELETE` |
| Categories | `GET /categories`, `GET /categories/{id}`, `POST`, `PUT`, `DELETE` |
| Orders | `GET /orders`, `GET /orders/{id}`, `POST`, `PUT` (status), `DELETE` |

## Examples

```bash
# List users
curl https://java-springboot.rodrigodip.com.br/users

# Create an order (price is snapshotted from the product)
curl -X POST https://java-springboot.rodrigodip.com.br/orders \
  -H 'Content-Type: application/json' \
  -d '{"clientId":1,"orderStatus":"WAITING_PAYMENT","items":[{"productId":1,"quantity":2}]}'
```

## Profiles

- `test` — H2 in-memory database with seed data (`TestConfig`)
- `dev` / `prod` — PostgreSQL (see `application-dev.properties.template`; real credentials are never committed)

Select with `spring.profiles.active` in `application.properties`.

## Run locally

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

## Roadmap

### ✅ Phase 0 — Course base (done)

- Spring Boot + JPA/Hibernate, layered architecture (resource/service/repository)
- H2 (test) + PostgreSQL (dev/prod) profiles, seed data, global exception handling

### ✅ Phase 1 — Extensions beyond the course (done)

- Full CRUD for products and categories (were read-only)
- Order creation via DTO (`clientId` + items, price snapshot, `@Transactional`), status update, delete
- Fixed product↔category mapping (`@ManyToMany`), 404s instead of 500s
- Password is write-only (never serialized in responses); accepted on create and update
- Corrected timestamp format (`HH:mm:ss`)

### 🔜 Phase 2 — Next (planned)

1. Bean Validation on inputs (`@NotBlank`, `@Email`, `@Positive`) + 400 handler
2. BCrypt password hashing
3. Controller tests (`@WebMvcTest`) + CI running tests instead of `-DskipTests`
4. Swagger UI (springdoc-openapi)
5. `POST /orders/{id}/payment` to complete the payment lifecycle
6. Java naming-convention cleanup (`FindAll` → `findAll`)

### 🔭 Future (out of scope for now)

- Authentication/authorization (Spring Security + JWT)
- Pagination/sorting, Flyway migrations, Docker, Actuator/observability

### Known limitations (honest scope)

- No auth — all endpoints are public
- Passwords are stored in plaintext (hashed: see Phase 2)
- No request validation yet (see Phase 2)
- Only a context-load test so far (see Phase 2)

---

# Web Services com Spring Boot e JPA / Hibernate

> Projeto de aprendizado do curso do Prof. Dr. Nelio Alves (devsuperior.com.br), estendido além do conteúdo do curso.
> **Demo ao vivo:** https://java-springboot.rodrigodip.com.br

**Objetivos do curso (concluídos)**

- Criar um projeto Spring Boot em Java
- Implementar o modelo de domínio
- Estruturar as camadas lógicas: resource, service, repository
- Configurar um banco de dados de teste (H2)
- Popular o banco de dados
- CRUD - Create, Retrieve, Update, Delete
- Tratamento de exceções

## O que esta API faz

API REST que gerencia Usuários (Users), Produtos (Products), Categorias (Categories) e Pedidos (Orders) — com itens, fluxo de status e pagamentos.

| Recurso | Endpoints |
|---|---|
| Usuários | `GET /users`, `GET /users/{id}`, `POST`, `PUT`, `DELETE` |
| Produtos | `GET /products`, `GET /products/{id}`, `POST`, `PUT`, `DELETE` |
| Categorias | `GET /categories`, `GET /categories/{id}`, `POST`, `PUT`, `DELETE` |
| Pedidos | `GET /orders`, `GET /orders/{id}`, `POST`, `PUT` (status), `DELETE` |

## Exemplos

```bash
# Listar usuários
curl https://java-springboot.rodrigodip.com.br/users

# Criar um pedido (o preço é copiado do produto no momento da criação)
curl -X POST https://java-springboot.rodrigodip.com.br/orders \
  -H 'Content-Type: application/json' \
  -d '{"clientId":1,"orderStatus":"WAITING_PAYMENT","items":[{"productId":1,"quantity":2}]}'
```

## Perfis

- `test` — banco H2 em memória com dados iniciais (`TestConfig`)
- `dev` / `prod` — PostgreSQL (veja `application-dev.properties.template`; credenciais reais nunca são commitadas)

Selecione com `spring.profiles.active` em `application.properties`.

## Executar localmente

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

## Roteiro

### ✅ Fase 0 — Base do curso (concluída)

- Spring Boot + JPA/Hibernate, arquitetura em camadas (resource/service/repository)
- Perfis H2 (teste) + PostgreSQL (dev/prod), dados iniciais, tratamento global de exceções

### ✅ Fase 1 — Extensões além do curso (concluídas)

- CRUD completo de produtos e categorias (antes eram só leitura)
- Criação de pedidos via DTO (`clientId` + itens, snapshot de preço, `@Transactional`), atualização de status, exclusão
- Correção do mapeamento produto↔categoria (`@ManyToMany`), 404 em vez de 500
- Senha é write-only (nunca serializada nas respostas); aceita na criação e na atualização
- Formato de data/hora corrigido (`HH:mm:ss`)

### 🔜 Fase 2 — Próximos passos (planejados)

1. Bean Validation nas entradas (`@NotBlank`, `@Email`, `@Positive`) + handler de 400
2. Hash de senha com BCrypt
3. Testes de controller (`@WebMvcTest`) + CI executando os testes em vez de `-DskipTests`
4. Swagger UI (springdoc-openapi)
5. `POST /orders/{id}/payment` para completar o ciclo de pagamento
6. Ajustes de convenção de nomes em Java (`FindAll` → `findAll`)

### 🔭 Futuro (fora do escopo por ora)

- Autenticação/autorização (Spring Security + JWT)
- Paginação/ordenação, migrations Flyway, Docker, Actuator/observabilidade

### Limitações conhecidas (escopo honesto)

- Sem autenticação — todos os endpoints são públicos
- Senhas armazenadas em texto puro (hash: ver Fase 2)
- Ainda sem validação das requisições (ver Fase 2)
- Por enquanto só há o teste de contexto (ver Fase 2)

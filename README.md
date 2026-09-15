# Web Services with Spring Boot and JPA / Hibernate

*Leia em [Português](#web-services-com-spring-boot-e-jpa--hibernate)*

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

## Web UI — recruiter showcase

The live demo opens on a server-rendered HTML frontend (Thymeleaf + HTMX + Tailwind, same origin at
`GET /?tab=client|admin`) built as the visual entry point to this project:

- **Client tab** — create your client, browse the paged catalog, place orders, track and cancel them.
- **Admin tab** — dashboard, full CRUD for products/categories/clients, order filters and status flow.

The JSON REST API is frozen as portfolio artifact; the HTML layer is purely additive — same services,
same JVM, zero CORS. Every interface decision is logged in [`docs/AD_manifest.md`](docs/AD_manifest.md).

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

### ✅ Phase 1.5 — Recruiter-facing web UI (done)

- Server-rendered HTML (Thymeleaf + HTMX + Tailwind) layered additively over the frozen JSON API
- Client/Admin virtual tabs: gated client creation, paged catalog (6/page), cart with toast feedback, order cancel; admin CRUD via modals, order search/filters, friendly error messages
- Full architecture/decision log: [`docs/AD_manifest.md`](docs/AD_manifest.md)

### 🔜 Phase 2 — Next (planned)

1. Bean Validation on inputs (`@NotBlank`, `@Email`, `@Positive`) + 400 handler
2. BCrypt password hashing
3. Controller tests (`@WebMvcTest`) + CI running tests instead of `-DskipTests`
4. Swagger UI (springdoc-openapi)
5. `POST /orders/{id}/payment` to complete the payment lifecycle
6. Java naming-convention cleanup (`FindAll` → `findAll`)

### 🔭 Future (out of scope for now)

- Authentication/authorization (Spring Security + JWT)
- Pagination/sorting at API level (the web catalog already paginates server-side), Flyway migrations, Docker, Actuator/observability

### Known limitations (honest scope)

- No auth — all endpoints are public (the web UI shows a `Demo — no auth` banner saying so)
- Passwords are stored in plaintext (hashed: see Phase 2)
- No request validation yet — web forms use manual checks only (see Phase 2)
- Only a context-load test so far (see Phase 2)

---

# Web Services com Spring Boot e JPA / Hibernate

*Read in [English](#web-services-with-spring-boot-and-jpa--hibernate)*

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

## Interface Web — vitrine para recrutadores

A demo ao vivo abre em um frontend HTML renderizado no servidor (Thymeleaf + HTMX + Tailwind, mesma origem em
`GET /?tab=client|admin`), construído como porta de entrada visual deste projeto:

- **Aba Client** — crie seu cliente, navegue pelo catálogo paginado, faça pedidos, acompanhe e cancele-os.
- **Aba Admin** — painel geral, CRUD completo de produtos/categorias/clientes, busca e filtros de pedidos, mensagens de erro amigáveis.

A API JSON está congelada como artefato de portfólio; a camada HTML é puramente aditiva — mesmos serviços,
mesma JVM, zero CORS. Todas as decisões de interface estão registradas em [`docs/AD_manifest.md`](docs/AD_manifest.md).

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

### ✅ Fase 1.5 — Interface web para recrutadores (concluída)

- HTML renderizado no servidor (Thymeleaf + HTMX + Tailwind) em camada aditiva sobre a API JSON congelada
- Abas virtuais Client/Admin: criação de cliente com trava de acesso, catálogo paginado (6/página), carrinho com feedback em toast, cancelamento de pedidos; CRUD de admin via modais, busca/filtros de pedidos, mensagens de erro amigáveis
- Registro completo de arquitetura/decisões: [`docs/AD_manifest.md`](docs/AD_manifest.md)

### 🔜 Fase 2 — Próximos passos (planejados)

1. Bean Validation nas entradas (`@NotBlank`, `@Email`, `@Positive`) + handler de 400
2. Hash de senha com BCrypt
3. Testes de controller (`@WebMvcTest`) + CI executando os testes em vez de `-DskipTests`
4. Swagger UI (springdoc-openapi)
5. `POST /orders/{id}/payment` para completar o ciclo de pagamento
6. Ajustes de convenção de nomes em Java (`FindAll` → `findAll`)

### 🔭 Futuro (fora do escopo por ora)

- Autenticação/autorização (Spring Security + JWT)
- Paginação/ordenação no nível da API (o catálogo web já pagina no servidor), migrations Flyway, Docker, Actuator/observabilidade

### Limitações conhecidas (escopo honesto)

- Sem autenticação — todos os endpoints são públicos (a interface web exibe um aviso `Demo — no auth`)
- Senhas armazenadas em texto puro (hash: ver Fase 2)
- Ainda sem validação das requisições — formulários web usam apenas verificações manuais (ver Fase 2)
- Por enquanto só há o teste de contexto (ver Fase 2)

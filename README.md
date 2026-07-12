# 🍽️ Quero Comida Hub

## 📚 Tech Challenge 2 — Pós-graduação em Arquitetura e Desenvolvimento Java (FIAP)

Backend para gestão de restaurantes e cardápios, construído do zero com **tipos de usuário**, **cadastro de restaurantes** e **itens de cardápio**, organizado sob os princípios de **Clean Architecture**.

O sistema distingue dois perfis de usuário — **Cliente** e **Dono de Restaurante** — e garante que apenas donos possam gerenciar seus próprios restaurantes e cardápios.

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 (sealed classes, records) |
| Framework | Spring Boot 3.5 |
| Banco de dados | MySQL 8.4 |
| Acesso a dados | Spring `JdbcClient` (sem ORM) |
| Migrações | Flyway |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers, ArchUnit |
| Cobertura | JaCoCo (mínimo de 80%) |
| Containerização | Docker + Docker Compose |

---

## 🚀 Como executar

**Pré-requisitos:** Docker e Docker Compose instalados. Java e Maven **não** são necessários localmente — o build e a execução acontecem dentro dos contêineres.

```bash
# 1. Clonar o repositório
git clone https://github.com/lfboeff/querocomidahub-tc2
cd querocomidahub

# 2. Subir os contêineres (build + start)
docker compose up --build
```

Com a aplicação em execução, os seguintes endereços ficam disponíveis:

| Recurso | URL |
|---|---|
| Base da API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Actuator (health) | http://localhost:9090/actuator/health |

```bash
# Encerrar a aplicação
docker compose down

# Encerrar e remover os volumes (limpa o banco de dados)
docker compose down -v
```

> As migrações Flyway e o *seed* inicial (tipos de usuário, usuários, restaurantes e itens de exemplo) rodam automaticamente na primeira inicialização.

---

## 🏛️ Arquitetura

O projeto adota **Clean Architecture**, organizado em **quatro verticais de negócio** independentes:

```
usertype   →  tipos de usuário (Cliente, Dono de Restaurante, ...)
user       →  usuários e sua associação a um tipo
restaurant →  restaurantes, com dono associado
menuitem   →  itens de cardápio, aninhados sob um restaurante
```

Cada vertical é dividida em três camadas, com dependências apontando sempre **para dentro** (infraestrutura → aplicação → domínio):

| Camada | Responsabilidade | Depende de |
|---|---|---|
| `domain/` | Entidades, regras de negócio, interfaces de gateway e exceções. Sem frameworks. | — (núcleo) |
| `application/` | Casos de uso, controllers de orquestração, DTOs e mappers. Sem detalhes de web ou persistência. | `domain` |
| `infrastructure/` | Gateways JDBC, controllers REST, Swagger, configuração Spring e resolução de identidade. | `application`, `domain` |

Estrutura de uma vertical típica (`restaurant`):

```
restaurant/
├── application/
│   ├── controller/     # RestaurantController — orquestra os use cases
│   ├── dto/            # Input/Output DTOs (records)
│   ├── mapper/         # DTO ↔ domínio
│   └── usecase/        # Create / GetById / List / Update / Delete
├── domain/
│   ├── exception/      # exceções de domínio da vertical
│   ├── gateway/        # IRestaurantGateway (interface)
│   └── model/          # Restaurant (entidade imutável)
└── infrastructure/
    ├── config/         # beans (@Configuration)
    ├── gateway/        # RestaurantJdbcGateway (JdbcClient)
    └── web/
        ├── controller/ # RestaurantApi (Swagger) + RestaurantApiController
        ├── json/       # Request/Response JSON (records)
        └── mapper/     # JSON ↔ DTO
```

O código transversal fica em **`shared/`**: abstração de logging, tratamento global de exceções (`GlobalExceptionHandler` com respostas RFC 7807), configuração do OpenAPI e o `UserIdentityResolver`.

As fronteiras entre camadas são verificadas automaticamente por **20 regras de arquitetura** com ArchUnit (ex.: `domain` não pode importar Spring; `application` não pode importar classes de web).

### Autorização simulada via `X-User-Id`

Endpoints que modificam restaurantes e cardápios exigem o header **`X-User-Id`**, que identifica o usuário chamador. O `UserIdentityResolver` resolve esse header para o usuário correspondente e as regras de negócio validam a permissão (ex.: apenas um Dono pode criar restaurantes; apenas o dono de um restaurante pode alterá-lo). Não há token/JWT nesta fase — é uma simulação de autenticação.

---

## 📡 Endpoints

Todos os endpoints usam o prefixo `/api/v1` e seguem a semântica REST. Respostas de erro seguem o padrão **RFC 7807** (ProblemDetail).

### Tipos de Usuário — `/api/v1/user-types`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/user-types` | Lista todos os tipos de usuário |
| `GET` | `/user-types/{id}` | Busca um tipo por ID |
| `POST` | `/user-types` | Cria um tipo de usuário |
| `PUT` | `/user-types/{id}` | Atualiza um tipo (tipos de sistema são protegidos) |
| `DELETE` | `/user-types/{id}` | Remove um tipo (bloqueado se estiver em uso) |

### Usuários — `/api/v1/users`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/users` | Lista todos os usuários |
| `GET` | `/users/{id}` | Busca um usuário por ID |
| `POST` | `/users` | Cria um usuário associado a um tipo |
| `PUT` | `/users/{id}` | Atualiza dados pessoais do usuário |
| `DELETE` | `/users/{id}` | Remove um usuário (bloqueado se for dono de restaurantes) |
| `PATCH` | `/users/{id}/user-type` | Reatribui o tipo do usuário |

### Restaurantes — `/api/v1/restaurants`

| Método | Endpoint | Header | Descrição |
|---|---|---|---|
| `GET` | `/restaurants` | — | Lista todos os restaurantes |
| `GET` | `/restaurants/{id}` | — | Busca um restaurante por ID |
| `POST` | `/restaurants` | `X-User-Id` | Cria um restaurante (chamador deve poder gerenciar restaurantes) |
| `PUT` | `/restaurants/{id}` | `X-User-Id` | Atualiza um restaurante (apenas o dono) |
| `DELETE` | `/restaurants/{id}` | `X-User-Id` | Remove um restaurante (apenas o dono) |

### Itens de Cardápio — `/api/v1/restaurants/{restaurantId}/menu-items`

| Método | Endpoint | Header | Descrição |
|---|---|---|---|
| `GET` | `/.../menu-items` | — | Lista os itens de um restaurante |
| `GET` | `/.../menu-items/{id}` | — | Busca um item por ID |
| `POST` | `/.../menu-items` | `X-User-Id` | Cria um item (apenas o dono do restaurante) |
| `PUT` | `/.../menu-items/{id}` | `X-User-Id` | Atualiza um item (apenas o dono) |
| `DELETE` | `/.../menu-items/{id}` | `X-User-Id` | Remove um item (apenas o dono) |

> Exemplos completos de requisição e resposta estão disponíveis na **Swagger UI** e na **coleção Postman**.

---

## 🗂️ Coleção Postman

Uma coleção pronta para importação está disponível em:

```
postman/QueroComidaHub_TC2.postman_collection.json
```

Organizada pelas quatro verticais, cada uma com pastas **Happy Path** e **Error Path**, cobrindo os cenários de sucesso e de erro (400, 401, 403, 404, 405, 409). Pré-configurada para `http://localhost:8080`. Compatível com Postman, Bruno e Insomnia.

> **Coleção autossuficiente.** Os cenários de **Happy Path** criam seus próprios dados (`POST`), capturam o `id` gerado em variáveis de coleção e operam (`PUT`/`PATCH`/`DELETE`) sobre a entidade recém-criada — nunca sobre os registros de *seed*. Assim o *seed* permanece intacto e a coleção roda **de ponta a ponta numa única passada, em qualquer ordem**, sem necessidade de resetar o banco entre execuções. Os itens de **Error Path** usam IDs inexistentes ou dados inválidos e também são independentes.
>
> Validação executável via [Newman](https://github.com/postmanlabs/newman): `npx newman run postman/QueroComidaHub_TC2.postman_collection.json` — 88 requisições, verdes contra o *seed* inicial.

---

## 🧪 Testes

```bash
# Testes unitários (não exigem Docker)
./mvnw test

# Testes unitários + integração + cobertura JaCoCo (exige Docker para os Testcontainers)
./mvnw verify
```

- **Unitários:** domínio, use cases, controllers (`@WebMvcTest`), mappers.
- **Integração (`*IT`):** gateways JDBC contra um MySQL real provisionado via **Testcontainers**.
- **Arquitetura:** 20 regras ArchUnit validando as fronteiras entre camadas.
- **Cobertura:** JaCoCo com limite mínimo de **80%**, verificado na fase `verify`.

---

## 📁 Estrutura do projeto

```
querocomidahub/
├── src/main/java/br/com/fiap/querocomidahub/
│   ├── usertype/         # vertical: tipos de usuário
│   ├── user/             # vertical: usuários
│   ├── restaurant/       # vertical: restaurantes
│   ├── menuitem/         # vertical: itens de cardápio
│   └── shared/           # logging, exceções, OpenAPI, X-User-Id
├── src/main/resources/
│   └── db/migration/     # migrações Flyway (V1–V5)
├── src/test/java/        # testes unitários, integração e arquitetura
├── postman/              # coleção Postman
├── Dockerfile            # build multi-stage (Maven → JRE Alpine)
├── docker-compose.yml    # serviços app + db
├── .env                  # variáveis de ambiente (commitado para fins acadêmicos)
└── pom.xml
```

---

## 👤 Autor

| Nome | RM |
|---|---|
| Luís Felipe Boeff | RM372311 |
# querocomidahub-tc2

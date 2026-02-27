# GestorFacil

API REST de gestao financeira para clientes bancarios, desenvolvida em **Java 25** com **Spring Boot 4.0.2**. Permite gerenciamento completo de usuarios e transacoes, analise de despesas, importacao/exportacao via Excel, autenticacao JWT stateless, integracao com APIs externas (conta bancaria e conversao de moedas) e geracao de relatorios.

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.0.2 |
| Seguranca | Spring Security + JWT (jjwt 0.11.5) |
| Persistencia | Spring Data JPA + PostgreSQL 16 |
| Cache/Sessoes | Redis 7 |
| Validacao | Hibernate Validator 9.0.0 |
| Excel | Apache POI 5.3.0 |
| Documentacao | springdoc-openapi 2.8.6 (Swagger UI) |
| Monitoramento | Spring Boot Actuator |
| Mock externo | WireMock 3.10.0 |
| Build | Maven |
| Containerizacao | Docker + Docker Compose |

---

## Pre-requisitos

- Docker e Docker Compose instalados
- (Opcional) Java 25 e Maven para desenvolvimento local sem Docker

---

## Como executar

### Via Docker Compose (recomendado)

```bash
cd GestorFacil
docker-compose up --build
```

Isso sobe 4 servicos:
- **app** (porta 8080) - API Spring Boot com perfil `docker`
- **db** (porta 5432) - PostgreSQL 16
- **redis** (porta 6379) - Redis 7
- **wiremock** (porta 8090) - Mock de APIs externas (conta bancaria)

### Desenvolvimento local

```bash
cd GestorFacil
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Requer PostgreSQL e Redis rodando localmente nas portas padrao (5432 e 6379).

---

## Como executar os testes

```bash
cd GestorFacil
./mvnw test
```

O projeto conta com **102 testes unitarios** cobrindo controllers, services e value objects.

---

## Documentacao da API (Swagger)

Com a aplicacao rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

A documentacao OpenAPI em JSON esta disponivel em:

```
http://localhost:8080/v3/api-docs
```

---

## Endpoints

### Saude (`/api`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/api/health` | Verificar status da aplicacao |

### Autenticacao (`/auth`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/auth/login` | Autenticar e obter tokens JWT |
| POST | `/auth/logout` | Invalidar tokens |
| POST | `/auth/refresh-token` | Renovar access token (header `X-Refresh-Token`) |
| GET | `/auth/confirma-email` | Confirmar email via token |
| POST | `/auth/senha/esqueci` | Solicitar recuperacao de senha |
| POST | `/auth/senha/reset` | Redefinir senha com token |

### Usuario (`/usuario`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/usuario/cadastro` | Cadastrar novo usuario |
| GET | `/usuario` | Consultar perfil autenticado |
| PUT | `/usuario` | Atualizar dados do usuario |
| DELETE | `/usuario` | Excluir conta |

### Transacoes (`/transacoes`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/transacoes` | Criar transacao |
| GET | `/transacoes` | Listar com filtros (tipo, categoria, periodo, paginacao) |
| GET | `/transacoes/{id}` | Buscar por ID |
| PUT | `/transacoes/{id}` | Atualizar transacao |
| DELETE | `/transacoes/{id}` | Deletar transacao |
| GET | `/transacoes/convertidas` | Listar com conversao de moeda |
| GET | `/transacoes/{id}/convertida` | Buscar por ID com conversao |
| POST | `/transacoes/importar` | Importar em massa via Excel (.xlsx) |

### Conta Bancaria (`/conta-bancaria`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/conta-bancaria/vincular` | Vincular conta ao usuario |
| DELETE | `/conta-bancaria/desvincular` | Desvincular conta |
| GET | `/conta-bancaria/saldo` | Consultar saldo |

### Analise Financeira (`/analises`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/analises/financeira` | Analise por periodo (BRL) |
| GET | `/analises/financeira/convertida` | Analise por periodo com conversao |

### Relatorios (`/relatorios`)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/relatorios/financeiro` | Download de relatorio Excel por periodo |

---

## Fluxo tipico de uso

1. **Cadastro**: `POST /usuario/cadastro` com nome, email e senha
2. **Confirmacao de email**: clicar no link recebido (ou `GET /auth/confirma-email?token=...`)
3. **Login**: `POST /auth/login` retorna access token e refresh token
4. **Criar transacoes**: `POST /transacoes` (header `Authorization: Bearer <token>`)
5. **Consultar analise**: `GET /analises/financeira?dataInicio=2025-01-01&dataFim=2025-12-31`
6. **Baixar relatorio**: `GET /relatorios/financeiro?dataInicio=2025-01-01&dataFim=2025-12-31`
7. **Importar transacoes**: `POST /transacoes/importar` com arquivo `.xlsx`

### Exemplo de payload - Cadastro

```json
POST /usuario/cadastro
{
  "nome": "Joao Silva",
  "email": "joao@email.com",
  "senha": "Senha@123"
}
```

### Exemplo de payload - Login

```json
POST /auth/login
{
  "email": "joao@email.com",
  "senha": "Senha@123"
}
```

---

## Importacao Excel

O endpoint `POST /transacoes/importar` aceita arquivos `.xlsx` com as seguintes colunas:

| Coluna | Tipo | Exemplo |
|--------|------|---------|
| descricao | Texto | Salario mensal |
| valor | Numerico | 5000.00 |
| tipo | Texto | CREDITO ou DEBITO |
| categoria | Texto | SALARIO, PIX, SAQUE, etc. |
| data | Data (dd/MM/yyyy) | 15/01/2025 |

Um modelo de planilha esta disponivel em `src/main/resources/templates/modelo-importacao-transacoes.xlsx`.

---

## Variaveis de ambiente

| Variavel | Descricao | Padrao |
|----------|-----------|--------|
| `JDBC_DATABASE_URL` | URL JDBC do PostgreSQL | - |
| `JDBC_DATABASE_USERNAME` | Usuario do banco | - |
| `JDBC_DATABASE_PASSWORD` | Senha do banco | - |
| `REDIS_HOST` | Host do Redis | localhost |
| `REDIS_PORT` | Porta do Redis | 6379 |
| `JWT_SECRET` | Chave secreta para tokens JWT | - |
| `EMAIL_MOCK` | Usar mock de email (true/false) | true |
| `INTEGRACAO_BANCO_URL` | URL base da API de conta bancaria | http://localhost:8090/v1 |
| `INTEGRACAO_CAMBIO_URL` | URL base da API de cambio | https://brasilapi.com.br/api/cambio/v1 |

---

## Estrutura do projeto

```
GestorFacil/
  src/main/java/com/JhonCodari/GestorFacil/
    config/          -> Configuracoes (Security, OpenAPI, Redis)
    controller/      -> Endpoints REST
    dto/             -> Objetos de transferencia de dados
    exception/       -> Excecoes e tratamento global
    model/           -> Entidades JPA e Value Objects
    repository/      -> Interfaces Spring Data JPA
    service/         -> Interfaces de servico
    service/impl/    -> Implementacoes de servico
  src/main/resources/
    application.properties
    application-dev.properties
    application-docker.properties
    templates/       -> Modelo de planilha Excel
  src/test/java/     -> Testes unitarios (102 testes)
```

---

## Limitacoes e proximos passos

### Limitacoes atuais
- O envio de emails utiliza apenas uma implementacao mock (`EmailServiceMockImpl`); nao ha envio real de emails
- Nao ha autenticacao multifator (MFA)
- Nao ha rate limiting nos endpoints
- A conversao de moedas depende da disponibilidade da Brasil API
- A integracao com conta bancaria depende do WireMock em ambiente de desenvolvimento

### Proximos passos
- Implementar envio real de emails (SMTP ou servico como SendGrid)
- Adicionar rate limiting para protecao contra abuso
- Implementar testes de integracao com banco de dados
- Adicionar testes de carga (JMeter ou k6) nos endpoints criticos
- Implementar paginacao na exportacao de relatorios para grandes volumes de dados
- Adicionar logs estruturados para auditoria

# books-change-api

API para conectar pessoas e lojas (sebos, livrarias, etc.) interessadas em trocar livros de forma segura e centralizada. Usuários podem cadastrar livros, associar categorias e, futuramente, propor trocas e combinar pontos de encontro — facilitando o contato entre pessoas e estabelecimentos.

## Status

- Em estágio inicial (MVP em progresso). As entidades já implementadas são **Usuários**, **Livros**, **Lojas**, **Endereços** e **Categorias**; o fluxo de propostas de troca (trades) ainda está no roadmap.
- Implementação atual: Java + Spring Boot (Maven). Estrutura do projeto segue princípios de Arquitetura Hexagonal (Ports & Adapters) de forma pragmática.

## Visão

- Centralizar e facilitar trocas de livros entre usuários e lojas.
- Prover segurança e confiança por meio de avaliações, pontos de encontro recomendados e controles de verificação.
- Facilitar integrações com serviços externos (mapas, notificações) no futuro.

## Stack principal

- **Linguagem:** Java 25 (LTS)
- **Framework:** Spring Boot 4 (Spring Framework 7)
- **Build:** Maven (wrapper `mvnw` incluído)
- **Persistência:** Spring Data JPA + H2 (banco em memória, uso atual em desenvolvimento — migração para SQL Server/Flyway está planejada)
- **Mapeamento:** MapStruct
- **Boilerplate:** Lombok
- **Testes:** JUnit 5 + Mockito
- **CI:** GitHub Actions (build + testes automatizados a cada PR/push em `master` e `develop`)
- **Organização do código:** Arquitetura Hexagonal, com pacotes principais em `api.domain`, `api.application`, `api.shared` e `infrastructure`
- Scripts úteis no repo: `common-feature-structure.sh` e `common-feature-structure.ps1` (geram a estrutura padrão de pastas para uma nova feature)

## Funcionalidades

**Implementado**
- CRUD de usuários, com ativação/inativação em cascata (livros e loja associados)
- CRUD de lojas, vinculadas a um usuário proprietário
- CRUD de livros, com categorias e condição atual do exemplar
- CRUD de categorias
- CRUD de endereços, com associação a lojas
- Listagem paginada e filtros de livros

**Roadmap**
- Autenticação/autorização de usuários
- Criação e gerenciamento de propostas de troca (trade requests)
- Ponto de encontro sugerido e mensagens entre partes
- Avaliações básicas (feedback de troca)
- Versionamento de rotas (`/api/v1/...`) e padronização de nomes de recursos no plural

## Arquitetura (resumo)

- Domínio no centro (entidades, exceptions) em `api.domain`.
- Casos de uso, ports (in/out) e adapters de cada feature em `api.application.<feature>`.
- Componentes utilitários e serviços compartilhados em `api.shared` e `infrastructure.shared` (normalizadores, exception handler, paginação, response builder).
- Fluxo: Controller → UseCase (port in) → Domínio → Port out → Adapter/Repositório → UseCase → Controller.
- Detalhes completos, convenções e exemplos de referência estão documentados em [`docs/architecture/architecture.md`](docs/architecture/architecture.md) e [`docs/architecture/patterns/patterns.md`](docs/architecture/patterns/patterns.md) (índice central dos padrões adotados).

## Endpoints atuais

> As rotas ainda não estão versionadas nem padronizadas no plural — isso está previsto no roadmap (ver seção acima). Abaixo, o estado atual:

- `POST /users/{userType}` — criar usuário
- `GET /users/{uuid}` — buscar usuário
- `PUT /users/{uuid}` — atualizar usuário
- `PUT /users/{uuid}/param/{param}` — ativar/inativar usuário
- `DELETE /users/{uuid}` — remover usuário
- `POST /store` — cadastrar loja
- `GET /store/{uuid}` — buscar loja
- `PUT /store/{uuid}` — atualizar loja
- `DELETE /store/{storeUuid}` — remover loja
- `POST /books` — publicar livro
- `GET /books/{uuid}` — buscar livro
- `GET /books/paged` — listar livros paginados
- `POST /category` — criar categoria
- `GET /category` — listar categorias
- `POST /address` — criar endereço
- `PUT /address/{uuid}` — atualizar endereço

Documentação detalhada de cada endpoint fica em `docs/endpoints/`.

## Getting started — desenvolvimento local

### Pré-requisitos
- Java 25 (JDK)
- Maven (opcional — o wrapper `./mvnw` já resolve a versão correta automaticamente)

### Build
- Com Maven wrapper:
  - Unix/macOS: `./mvnw clean package`
  - Windows: `mvnw.cmd clean package`
- Ou com Maven local:
  - `mvn clean package`

### Rodar localmente
- Usando Spring Boot:
  - `./mvnw spring-boot:run`
- Ou executando o jar gerado:
  - `java -jar target/bookschange-<versão>.jar`

### Configuração
- Arquivo principal: `src/main/resources/application.yaml`
- Atualmente o projeto roda com H2 em memória, sem necessidade de configuração adicional para subir localmente.
- Para ambientes específicos, crie um `application-<profile>.yaml` e ative com `-Dspring.profiles.active=<profile>`.

### Testes
- Unitários com JUnit 5 + Mockito, seguindo padrão AAA.
- Rodar:
  - `./mvnw test`

## Documentação da API

Ainda não há OpenAPI/Swagger configurado no projeto — é uma melhoria recomendada para próximas versões (dependência `springdoc-openapi`). Enquanto isso, a documentação de endpoints, arquitetura e padrões vive em `docs/`, com `docs/architecture/patterns/patterns.md` como índice central.

## Logs & Monitoramento

- Logging via SLF4J + Logback.
- Logs de orquestração concentrados nos use-cases para facilitar rastreabilidade.
- Futuro: integração com ferramentas de APM (Prometheus/Grafana, Elastic, Sentry).

## Security (visão inicial)

- Autenticação/Autorização: planejada (JWT ou OAuth2).
- Sanitização e validação de inputs via Bean Validation nos DTOs de entrada.
- Pontos de encontro sugeridos: regras de privacidade e segurança ainda a serem definidas.

## Contribuindo

Leia [`docs/branch-commits/branch-and-commit-guide.md`](docs/branch-commits/branch-and-commit-guide.md) para convenções de branches e commits, e [`docs/features/feature-development-guide.md`](docs/features/feature-development-guide.md) para o passo a passo de desenvolvimento de uma nova feature.

Workflow sugerido:
1. Criar/vincular uma issue descrevendo a feature ou bug.
2. Criar branch seguindo a convenção (`feature/<issue>-<descrição-curta>`, `bug/<issue>-<descrição>`, etc.).
3. Implementar seguindo a arquitetura hexagonal; escrever testes unitários.
4. Abrir PR com descrição, checklist preenchido e testes cobrindo a mudança.

Checklist mínimo no PR:
- [ ] Issue vinculada
- [ ] Testes unitários adicionados
- [ ] Documentação atualizada (`docs/endpoints/`, `docs/architecture/`, README quando aplicável)
- [ ] CI (`Build & Tests`) passando

## Roadmap / Ideias futuras

- Versionamento de rotas (`/api/v1/...`) e padronização de nomes no plural
- Autenticação e verificação de usuário (e-mail/telefone)
- Migração de persistência para SQL Server + Flyway
- Containerização com Docker e publicação de imagem via CI/CD
- Fluxo completo de propostas de troca entre usuários/lojas
- Integração com mapas para sugerir pontos de encontro seguros
- Sistema de reputação / avaliações
- Notificações (e-mail, push)
- Documentação automática via OpenAPI/Swagger
- Mobile app / frontend público

## Arquivos úteis no repositório

- `common-feature-structure.sh` / `common-feature-structure.ps1` — scripts para gerar a estrutura inicial de pastas de uma nova feature.
- `docs/` — documentação de arquitetura, padrões, endpoints e convenções de branch/commit.

## Licença

- (Escolha a licença que preferir, ex.: MIT, Apache-2.0). Adicione um arquivo `LICENSE` no repositório.

## Contato

- Autor / Maintainer: [@dacruuz](https://github.com/dacruuz) (GitHub).
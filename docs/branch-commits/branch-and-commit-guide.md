<a name="branch-and-commit-guide"></a>
# Padrões de Branches e Commits — books-change-api

## Objetivo
- Definir convenções claras e separadas para nomes de branches e mensagens de commit, garantindo consistência e rastreabilidade.

## Sumário
- Branches
- Convenções de commit
- Pull Request & Merge

---

## 1) Branches

### Branches principais
- `master` — branch estável, pronta para produção.
- `develop` — branch de integração (usada para testes).

### Tipos de branches de trabalho e formato
- feature: `feature/<ISSUE>-<descrição-curta>`
    - Uso: novas funcionalidades.
    - Ex.: `feature/0033-endereco-crud`
- bug: `bug/<ISSUE>-<descrição-curta>`
    - Uso: correções de bugs relacionadas a uma issue.
    - Ex.: `bug/0015-cpf-unique`
- hotfix: `hotfix/<descrição-curta>`
    - Uso: correções urgentes diretamente em produção; partir de `master`.
    - Ex.: `hotfix/nullpointer-store`
- release: `release/<versão>`
    - Uso: preparação de release (ex.: `release/1.2.0`).
- chore: `chore/<ISSUE>-<descrição>`
    - Uso: tarefas de manutenção (dependências, build).
    - Ex.: `chore/update-deps`
- docs: `docs/<ISSUE>-<descrição>`
    - Uso: alterações somente em documentação.
    - Ex.: `docs/add-architecture-md`
- test: `test/<ISSUE>-<descrição>`
    - Uso: mudanças relacionadas a testes.
- refactor: `refactor/<ISSUE>-<descrição>`
    - Uso: mudanças arquitetural ou escrita que não alteração o fluxo.
- improvement: `improvement/<ISSUE>-<descrição>
    - Uso: melhoria no código (legilibilidade, fluxo, etc.).
    - Ex.: `improvement/<ISSUE>-melhorando-fluxo-cadastro-livro`
---

## 2) Convenções de commit

Os commits devem seguir uma convenção baseada no padrão Conventional Commits.

### Formato
`<tipo>(<escopo>): <descrição>`

Exemplo:

`feat(book): adiciona endpoint para cadastro de livro`

O escopo é opcional, mas deixa seu commit mais descritivo:

`feat: adiciona endpoint para cadastro de livro`

## Assinatura para uma issue

Normalmente, o commit que será feita vai estar relacionado à alguma issue no board no github.
Então, o padrão adotado para isso é o seguinte:
`#<issue> - <tipo>(<escopo>): <descrição>`

Exemplo:

`#67 - feat(doc) adicionando documentação.`

Escrever assim faz com que os commits se relacionem à issue pelo número dela.
O próprio github faz isso, isso criar um vínculo (um link é adicionado) quando o PR está atribuído à issue.

### Tipos de commit

| Tipo          | Uso |
|---------------|-----|
| `feat`        | Nova funcionalidade |
| `fix`         | Correção de bug |
| `refactor`    | Refatoração sem alteração de comportamento |
| `test`        | Criação ou alteração de testes |
| `docs`        | Alterações na documentação |
| `chore`       | Manutenção, dependências e configurações |
| `build`       | Alterações relacionadas ao processo de build |
| `ci`          | Alterações em CI/CD |
| `perf`        | Melhorias de performance |
| `style`       | Formatação ou alterações que não afetam a lógica |
| `improvement` | Melhoria no código |

## 3) Pull Request & Merge

### Descrição
Os PR's são abertos e vinculados à issue no board do github.
É necessário a abertura de dois Pull Requests, que são para as branches principais `master` e `develop`.

### PR branch `master`
O nome da branch é a convenção descrita nesse mesmo documento. Aqui é onde a atividade é feita normalmente.

- Criar branch seguindo a convenção;
- Implementar a atividade;
- Abrir PR e vincular à issue.

### PR branch `develop`
Aqui é aberto um PR apontando para a branch `develop`. O padrão de nomemclatura utilizado é `integration/<nome-branch-master>`.
Aqui já considere que o passo a passo do padrão da `master` foi realizado.

- Criar branch para `develop` com o prefixo `integration/`;
- Fazer o merge da branch da `master` para a branch de `integration`;
- Abrir PR apontando para a `develop` e vincular à issue.


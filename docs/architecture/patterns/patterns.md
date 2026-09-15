# Introdução

Este documento reúne os padrões de projeto e as convenções adotadas neste repositório para orientar implementações coerentes com a arquitetura Hexagonal/Clean aplicada ao sistema. Ele destina‑se a desenvolvedores, revisores de código e novos colaboradores que precisam entender como estruturar artefatos (DTOs, controllers, ports, use‑cases, adapters, repositories), como normalizar e validar dados, e como reaproveitar serviços de aplicação.

O objetivo é reduzir ambiguidade e duplicação, padronizar decisões recorrentes (ex.: uso de `record` para DTOs, MapStruct para mapeamento, Normalizers para padronização de texto) e documentar práticas recomendadas para testes e tratamento de exceções. Cada seção traz responsabilidades, pontos de atenção, recomendações de teste e links para implementações de referência no código.

Use este arquivo como referência ao criar novos endpoints ou ao revisar PRs; quando houver necessidade de exemplos ou detalhes de implementação, consulte os mappers/normalizers/validators referenciados nos links. Para alterar ou complementar um padrão, registre a motivação na issue correspondente e atualize este documento via PR para manter o histórico e a rastreabilidade.

---

## Sumário
- [Integração entre os padrões (fluxo recomendado dentro do UseCase)](#integração-entre-os-padrões-fluxo-recomendado-dentro-do-usecase)
- [Testes e validação de qualidade (resumo por padrão)](#testes-e-validação-de-qualidade-resumo-por-padrão)
- [Riscos e recomendações finais](#riscos-e-recomendações-finais)
- [Links para busca adicional no repositório](#links-para-busca-adicional-no-repositório-caso-queira-revisar-mais-ocorrências)

---

# Integração entre os padrões (fluxo recomendado dentro do UseCase)

## Ordem típica:
1. Controller valida request (Bean Validation).
2. Mapper (MapStruct) converte `request` → `domain/entity` ou `input DTO`.
3. `Validator` executa validações de negócio (unicidade, formato lógico).
4. `Normalizer` específico por feature normaliza o domain object.
5. UseCase usa `ports out` para buscar dependências e persistir.
6. Service compartilhado (ex.: `StoreDeletionService`) pode ser chamado para operações compostas/reusáveis.
7. Mapper converte `entity` → `response`; Controller retorna resposta.

## Transações
- Preferir anotar `@Transactional` na camada de aplicação (UseCase ou Application Service) que orquestra escrita.

## MapStruct
- `MapStruct` é usado para manter mapeamentos limpos e testáveis; mappers pequenos por feature são preferíveis.
- Não misturar normalização dentro do mapper — executar normalização explicitamente no UseCase.

---

# Testes e validação de qualidade (resumo por padrão)

- **BaseModel**: testes de persistência básica e hooks (`@PrePersist`).
  - [BaseModel](models/base-model.md)
- **SelectOptionDTO + mappers**: testar conversões de coleções.
  - [SelectOptionDTO](dtos/select-option-dto.md)
- **TextNormalizer**: testes unitários cobrindo todos os métodos (cpf, cnpj, telefone, zip, upper/lower).
  - [TextNormalizer](services/text-normalizer.md)
- **Feature Normalizers**: testar que aplicam transformações corretas ao domain object.
- **Validators**: testar caminhos positivos e negativos (existência, unicidade). Não é padrão para caso em que essa regra é validada em apenas um local.
  - [Validators](services/validators.md)
- **StoreDeletionService**: testar cenários (loja sem endereço, proprietário sem loja, falha na deleção de endereço) e que efeitos colaterais ocorrem conforme esperado.
  - [StoreDeletionService](services/store-deletion-service.md)
- **UseCases**: mockar ports out e verificar que normalizer e validator são chamados, que transação cobre a operação e que exceptions são lançadas corretamente.
- **Padrão AAA e convenções de testes unitários**: veja o guia dedicado com o detalhamento do padrão Arrange-Act-Assert, convenções de nomenclatura e boas práticas com Mockito adotadas no projeto.
  - [Testes Unitários (AAA)](../../tests/unit-tests.md)

---

# Riscos e recomendações finais

- **Validação em records**: confirmar compatibilidade do Hibernate Validator com `record`/versão do projeto.
- **Lombok + JPA**: documentar e aplicar práticas seguras para evitar problemas com proxies JPA.
- **Normalização**: documentar campos normalizados e garantir idempotência; evitar perda de dados não-intencional.
- **Application Services reutilizáveis**: mantê-los focados, bem testados e com contratos claros para evitar acoplamento excessivo entre UseCases.

---

# Links para busca adicional no repositório (caso queira revisar mais ocorrências)

- Buscar “Normalizer": https://github.com/dacruuz/books-change-api/search?q=Normalizer&type=code
- Buscar “BaseModel": https://github.com/dacruuz/books-change-api/search?q=BaseModel&type=code
- Buscar “SelectOptionDTO": https://github.com/dacruuz/books-change-api/search?q=SelectOptionDTO&type=code
- Buscar “Validator": https://github.com/dacruuz/books-change-api/search?q=Validator&type=code
- Buscar “StoreDeletionService": https://github.com/dacruuz/books-change-api/search?q=StoreDeletionService&type=code
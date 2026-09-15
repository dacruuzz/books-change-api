<a name="unit-tests"></a>
# Testes Unitários — books-change-api

## Propósito
Este documento descreve como os testes unitários são escritos no projeto, com foco no padrão **AAA (Arrange-Act-Assert)** — a base estrutural de todos os testes do repositório — além das convenções de nomenclatura, ferramentas e boas práticas adotadas.

Serve como referência para quem for escrever novos testes ou revisar PRs que os alterem.

---

## Sumário
- [Padrão AAA (Arrange-Act-Assert)](#padrão-aaa)
- [Exemplo prático](#exemplo-prático)
- [Convenções de estrutura e nomenclatura](#convenções-de-estrutura-e-nomenclatura)
- [Uso do Mockito](#uso-do-mockito)
- [O que se testa em cada tipo de classe](#o-que-se-testa-em-cada-tipo-de-classe)
- [Boas práticas e aprendizados do projeto](#boas-práticas-e-aprendizados-do-projeto)
- [Código de referência](#código-de-referência)

---

## Padrão AAA

Todos os testes do projeto seguem o padrão **AAA — Arrange, Act, Assert** (equivalente ao Given-When-Then). A ideia é dividir cada método de teste em três blocos bem definidos e sequenciais, facilitando a leitura e a manutenção.

### Arrange (organizar)
Prepara todo o cenário necessário para o teste:
- Instancia objetos reais quando o UseCase **muta o estado** deles via setters.
- Configura mocks (`@Mock`) para dependências injetadas e portas externas (`ports out`).
- Define os `when(...).thenReturn(...)` / `doNothing()` / `doThrow(...)` necessários para simular o comportamento dos colaboradores.

### Act (agir)
Executa o método/comportamento que está sendo testado — geralmente uma única chamada ao método público da classe sob teste (`useCase.create(request)`, `service.normalizeData(book)`, etc.).

### Assert (verificar)
Confirma que o resultado é o esperado:
- `assertEquals`, `assertThrows` para valores de retorno e exceções.
- `verify(mock, times(n))`, `verify(mock, never())`, `verifyNoInteractions()` para confirmar interações com os colaboradores.

Em alguns testes mais antigos, os blocos são marcados explicitamente com comentários (`// --- ARRANGE`, `// --- ACT`, `// --- ASSERT`); em testes mais novos, a divisão fica implícita pela ordem do código, sem necessidade de comentários redundantes. Ambos os estilos coexistem no projeto — a prioridade é manter a sequência lógica AAA, não necessariamente o comentário.

---

## Exemplo prático

Extraído de `CreateCategoryUseCaseTest`:

```java
@Test
@DisplayName("Deve criar uma nova categoria com sucesso")
void shouldCreateNewCategorySuccessfully() {
    // --- ARRANGE ---
    when(normalizer.normalizeToLowerCase(request.slug())).thenReturn(VALID_SLUG_LOWERCASED);
    when(findCategoryPortOut.existsBySlug(VALID_SLUG_LOWERCASED)).thenReturn(false);
    when(mapper.createCategoryToEntity(request)).thenReturn(mappedCategory);
    when(normalizer.normalizeToLowerCase(mappedCategory.getSlug())).thenReturn(VALID_SLUG_LOWERCASED);
    when(normalizer.normalizeToUpperCase(mappedCategory.getLabel())).thenReturn(VALID_LABEL_UPPERCASED);
    when(saveCategoryPortOut.save(mappedCategory)).thenReturn(mappedCategory);

    CategoryResponse expectedResponse = mock(CategoryResponse.class);
    when(mapper.entityToCategoryResponse(mappedCategory)).thenReturn(expectedResponse);

    // --- ACT ---
    CategoryResponse result = useCase.create(request);

    // --- ASSERT ---
    assertEquals(expectedResponse, result);

    ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
    verify(saveCategoryPortOut).save(categoryCaptor.capture());

    Category savedCategory = categoryCaptor.getValue();
    assertEquals(VALID_LABEL_UPPERCASED, savedCategory.getLabel());
    assertEquals(VALID_SLUG_LOWERCASED, savedCategory.getSlug());
}
```

Note que o **Arrange** cobre toda a cadeia de chamadas que o UseCase faz (normalizer → validator → mapper → port out), o **Act** é uma única linha, e o **Assert** combina verificação de valor de retorno com `ArgumentCaptor` para inspecionar o estado da entidade no momento do `save()`.

---

## Convenções de estrutura e nomenclatura

- **Anotações**: `@ExtendWith(MockitoExtension.class)`, `@Mock` para dependências, `@InjectMocks` para a classe sob teste.
- **`@DisplayName`**: toda classe de teste usa `@DisplayName` em português, descrevendo o cenário testado de forma legível.
- **Nomenclatura de métodos**: `shouldXxx` (em inglês), descrevendo o comportamento esperado — ex.: `shouldCreateNewCategorySuccessfully`, `shouldThrowNotFoundExceptionWhenBookIsNotFoundByUuid`.
    - Alguns arquivos mais antigos usam o padrão em português `metodo_cenario_resultadoEsperado`; nesses casos, mantém-se a convenção já existente no arquivo em vez de forçar a padronização.
- **`@BeforeEach`**: usado para centralizar a criação de objetos e dados compartilhados entre os testes de uma classe (`setUp()`).
- **Mocks de objetos cujo conteúdo não importa**: `mock(Category.class)` em vez de instanciar e popular manualmente, quando os valores internos não afetam o teste.

---

## Uso do Mockito

Alguns pontos de atenção recorrentes no projeto:

- **Mockar o que é injetado, instanciar o que é mutado**: dependências (`ports`, `mappers`, `normalizers`) são mocks; entidades de domínio que o UseCase altera via setters são instâncias reais.
- **Stub da cadeia completa**: quando o UseCase encadeia uma normalização antes de repassar o valor a um mock de port, é preciso configurar os stubs de **todas** as etapas da cadeia — não apenas da última chamada.
- **Métodos void**: `doThrow(...).when(mock).metodoVoid(...)` — `when(...).thenThrow(...)` não compila para métodos void.
- **Exceptions em stubs**: usar `thenThrow(new NotFoundException("mensagem"))` em vez de `thenThrow(NotFoundException.class)`, já que a exceção não possui construtor sem argumentos.
- **`verify()` e timing de argumentos**: argumentos que referenciam `objeto.getXxx()` dentro de `verify()` são avaliados **depois** que o método sob teste já mutou o objeto — por isso o projeto usa `anyString()`/`any()` como convenção nesses casos, ou captura o valor original antes da execução.
- **`doNothing()` é redundante** em métodos void — o comportamento padrão do Mockito já não faz nada.
- **`ArgumentCaptor`**: usado propositalmente para inspecionar o estado da entidade no momento do `save()`, e não apenas confirmar que o método foi chamado.

---

## O que se testa em cada tipo de classe

| Tipo de classe        | O que é verificado |
|------------------------|---------------------|
| **UseCase**            | Orquestração: se os colaboradores certos foram chamados, na ordem certa, e se exceções de negócio são lançadas corretamente. **Não** se valida o resultado de lógica que pertence a um colaborador mockado (ex.: normalizer mockado não normaliza nada de verdade). |
| **Normalizer**         | Se cada campo relevante da entidade recebe a chamada correta do `TextNormalizer` (via `verify`). |
| **Validator**          | Cenários positivos e negativos de validação, e a exceção/mensagem lançada em cada caso. |
| **Service compartilhado** (ex.: `StoreDeletionService`) | Efeitos colaterais esperados (ex.: exclusão de endereço, mudança de tipo de usuário) e comportamento em cenários alternativos (sem endereço, sem loja, etc.). |

---

## Boas práticas e aprendizados do projeto

- Testar **apenas comportamento público** — métodos privados são testados indiretamente através da API pública da classe.
- **Records são imutáveis**: para variar um único campo em múltiplos cenários de teste, cria-se uma nova instância do record (opcionalmente via método helper privado), nunca se tenta atribuir campo diretamente.
- **Ports sem múltiplas implementações** (ex.: `StoreDeletionService`) não precisam de interface dedicada — isso reflete diretamente na forma como são testados (sem necessidade de mocks de porta para eles).
- Testes de UseCase sempre cobrem, no mínimo:
    1. O caminho de sucesso (happy path).
    2. Pelo menos um cenário de exceção de "não encontrado" (`NotFoundException`).
    3. Verificação de que operações subsequentes **não ocorrem** quando uma validação falha (`verify(mock, never())`).

---

## Código de referência
- [CreateCategoryUseCaseTest.java](https://github.com/dacruuz/books-change-api/blob/master/src/test/java/br/com/bookschange/api/application/category/usecases/CreateCategoryUseCaseTest.java)
- [UpdateBookUseCaseTest.java](https://github.com/dacruuz/books-change-api/blob/master/src/test/java/br/com/bookschange/api/application/book/usecases/UpdateBookUseCaseTest.java)
- [DeleteCategoryUseCaseTest.java](https://github.com/dacruuz/books-change-api/blob/master/src/test/java/br/com/bookschange/api/application/category/usecases/DeleteCategoryUseCaseTest.java)
- [BookNormalizerTest.java](https://github.com/dacruuz/books-change-api/blob/master/src/test/java/br/com/bookschange/api/application/book/services/BookNormalizerTest.java)
- [BookValidatorTest.java](https://github.com/dacruuz/books-change-api/blob/master/src/test/java/br/com/bookschange/api/application/book/services/BookValidatorTest.java)
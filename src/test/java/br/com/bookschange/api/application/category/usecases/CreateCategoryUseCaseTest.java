package br.com.bookschange.api.application.category.usecases;

import br.com.bookschange.api.application.category.adapters.in.dtos.request.CreateCategoryRequest;
import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponse;
import br.com.bookschange.api.application.category.mappers.CategoryMapper;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.application.category.ports.out.SaveCategoryPortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Category;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {

    private static final String VALID_SLUG = "ficcao-cientifica";
    private static final String VALID_SLUG_LOWERCASED = "ficcao-cientifica";
    private static final String VALID_LABEL = "Ficcão Científica";
    private static final String VALID_LABEL_UPPERCASED = "FICÇÃO CIENTÍFICA";

    @Mock private CategoryMapper mapper;
    @Mock private TextNormalizer normalizer;
    @Mock private SaveCategoryPortOut saveCategoryPortOut;
    @Mock private FindCategoryPortOut findCategoryPortOut;

    @InjectMocks
    private CreateCategoryUseCase useCase;

    private CreateCategoryRequest request;
    private Category mappedCategory;

    @BeforeEach
    void setUp() {
        request = new CreateCategoryRequest(
                VALID_LABEL,
                VALID_SLUG,
                "Livros de ficção científica"
        );

        mappedCategory = new Category();
        mappedCategory.setLabel(request.label());
        mappedCategory.setSlug(request.slug());
        mappedCategory.setDescription(request.description());
    }

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
        verify(findCategoryPortOut).existsBySlug(VALID_SLUG_LOWERCASED);
        verify(normalizer, times(1)).normalizeToUpperCase(anyString());
        verify(normalizer, times(2)).normalizeToLowerCase(anyString());
    }

    @Test
    @DisplayName("Deve retornar uma exception quando o slug já existe")
    void shouldThrowBusinessExceptionWhenSlugAlreadyExists() {
        // --- ARRANGE ---
        when(normalizer.normalizeToLowerCase(request.slug())).thenReturn(VALID_SLUG_LOWERCASED);
        when(findCategoryPortOut.existsBySlug(VALID_SLUG_LOWERCASED)).thenReturn(true);

        // --- ACT + ASSERT ---
        BusinessException e = assertThrows(BusinessException.class, () -> useCase.create(request));

        // --- ASSERT ---
        assertEquals("Já existe uma categoria cadastrada com esse identificador", e.getMessage());
        verify(normalizer, times(1)).normalizeToLowerCase(anyString());
        verify(findCategoryPortOut, times(1)).existsBySlug(anyString());
        verify(saveCategoryPortOut, never()).save(any());
        verify(mapper, never()).createCategoryToEntity(any());
        verify(mapper, never()).entityToCategoryResponse(any());
    }

    @Test
    @DisplayName("Não deve normalizar label quando for null")
    void shouldNotNormalizeLabelWhenIsNull() {
        // --- ARRANGE
        mappedCategory.setLabel(null);

        when(mapper.createCategoryToEntity(request)).thenReturn(mappedCategory);
        when(normalizer.normalizeToLowerCase(anyString())).thenReturn(VALID_SLUG_LOWERCASED);
        when(saveCategoryPortOut.save(mappedCategory)).thenReturn(mappedCategory);
        when(mapper.entityToCategoryResponse(mappedCategory)).thenReturn(mock(CategoryResponse.class));

        // --- ACT
        useCase.create(request);

        // --- ASSERT
        verify(normalizer, times(2)).normalizeToLowerCase(anyString());
        verify(normalizer, never()).normalizeToUpperCase(anyString());
    }

    @Test
    @DisplayName("Não deve normalizar slug quando for null")
    void shouldNotNormalizeSlugWhenIsNull() {
        // --- ARRANGE
        mappedCategory.setSlug(null);

        when(normalizer.normalizeToLowerCase(request.slug())).thenReturn(VALID_SLUG_LOWERCASED);
        when(findCategoryPortOut.existsBySlug(VALID_SLUG_LOWERCASED)).thenReturn(false);
        when(mapper.createCategoryToEntity(request)).thenReturn(mappedCategory);
        when(normalizer.normalizeToUpperCase(anyString())).thenReturn(VALID_LABEL_UPPERCASED);
        when(saveCategoryPortOut.save(mappedCategory)).thenReturn(mappedCategory);
        when(mapper.entityToCategoryResponse(mappedCategory)).thenReturn(mock(CategoryResponse.class));

        // --- ACT
        useCase.create(request);

        // --- ASSERT
        verify(normalizer, times(1)).normalizeToLowerCase(anyString());
        verify(normalizer, times(1)).normalizeToUpperCase(anyString());
    }
}

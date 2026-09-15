package br.com.bookschange.api.application.category.usecases;

import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponse;
import br.com.bookschange.api.application.category.mappers.CategoryMapper;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCategoryUseCaseTest {

    @Mock private CategoryMapper mapper;
    @Mock private FindCategoryPortOut findCategoryPortOut;

    @InjectMocks
    FindCategoryUseCase useCase;

    private UUID categoryUuid;
    private Category category;
    private List<Category> categoryList;
    private CategoryResponse expectedResponse;
    private List<CategoryResponse> expectedResponseList;

    @BeforeEach
    void setUp() {
        categoryUuid = UUID.randomUUID();
        category = mock(Category.class);
        category.setUuid(categoryUuid);
        expectedResponse = mock(CategoryResponse.class);

        categoryList = new ArrayList<>();
        categoryList.add(category);

        expectedResponseList = new ArrayList<>();
        expectedResponseList.add(expectedResponse);
    }

    @Test
    @DisplayName("Deve buscar todas as categorias com sucesso")
    void shouldFindAllCategoriesSuccessfully() {
        // --- ARRANGE
        when(findCategoryPortOut.findAll()).thenReturn(categoryList);
        when(mapper.entityToCategoryResponse(category)).thenReturn(expectedResponse);

        // --- ACT
        List<CategoryResponse> result = useCase.findAll();

        // --- ASSERT
        assertEquals(expectedResponseList, result);
        verify(findCategoryPortOut).findAll();
        verify(mapper).entityToCategoryResponse(category);
    }

    @Test
    @DisplayName("Deve buscar uma categoria pelo uuid com sucesso")
    void shouldFindCategoryByUuidSuccessfully() {
        // --- ARRANGE
        when(findCategoryPortOut.findByUuidOrThrow(categoryUuid)).thenReturn(category);
        when(mapper.entityToCategoryResponse(category)).thenReturn(expectedResponse);

        // --- ACT
        CategoryResponse result = useCase.findByUuid(categoryUuid);

        // --- ASSERT
        assertEquals(expectedResponse, result);
        verify(findCategoryPortOut).findByUuidOrThrow(categoryUuid);
        verify(mapper).entityToCategoryResponse(category);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando não encontrar uma categoria pelo UUID")
    void shouldThrowNotFoundExceptionWhenCategoryIsNotFoundByUuid() {
        // --- ARRANGE
        when(findCategoryPortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Categoria não encontrada"));

        // --- ACT + ASSERT
        NotFoundException e = assertThrows(NotFoundException.class, () -> useCase.findByUuid(categoryUuid));

        // --- ASSERT
        assertEquals("Categoria não encontrada", e.getMessage());
        verify(findCategoryPortOut).findByUuidOrThrow(any());
        verify(mapper, never()).entityToCategoryResponse(any());
    }
}
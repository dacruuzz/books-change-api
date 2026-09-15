package br.com.bookschange.api.application.category.usecases;

import br.com.bookschange.api.application.bookcategory.ports.out.DeleteBookCategoryPortOut;
import br.com.bookschange.api.application.bookcategory.ports.out.FindBookCategoryPortOut;
import br.com.bookschange.api.application.category.ports.out.DeleteCategoryPortOut;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.BookCategory;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock private FindCategoryPortOut findCategoryPortOut;
    @Mock private DeleteCategoryPortOut deleteCategoryPortOut;
    @Mock private FindBookCategoryPortOut findBookCategoryPortOut;
    @Mock private DeleteBookCategoryPortOut deleteBookCategoryPortOut;

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    private final UUID categoryUuid = UUID.randomUUID();
    private Category category;
    private List<BookCategory> bookCategoryList;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setUuid(categoryUuid);

        Book book = new Book();
        book.setUuid(UUID.randomUUID());

        BookCategory bookCategory = new BookCategory();
        bookCategory.setUuid(UUID.randomUUID());
        bookCategory.setBook(book);
        bookCategory.setCategory(category);

        bookCategoryList = new ArrayList<>();
        bookCategoryList.add(bookCategory);
    }

    @Test
    @DisplayName("Deve excluir uma categoria com livros associados com sucesso")
    void shouldDeleteCategoryWithBookCategoriesSuccessfully() {
        // --- ARRANGE
        when(findCategoryPortOut.findByUuidOrThrow(categoryUuid)).thenReturn(category);
        when(findBookCategoryPortOut.findAllByCategoryUuid(category.getUuid())).thenReturn(bookCategoryList);
        doNothing().when(deleteBookCategoryPortOut).deleteAll(bookCategoryList);
        doNothing().when(deleteCategoryPortOut).delete(category);

        // --- ACT
        useCase.delete(categoryUuid);

        // --- ASSERT
        verify(deleteBookCategoryPortOut).deleteAll(bookCategoryList);
        verify(deleteCategoryPortOut).delete(category);
    }

    @Test
    @DisplayName("Deve excluir uma categoria sem livros associados com sucesso")
    void shouldDeleteCategoryWithoutBookCategoriesSuccessfully() {
        // --- ARRANGE
        when(findCategoryPortOut.findByUuidOrThrow(categoryUuid)).thenReturn(category);
        when(findBookCategoryPortOut.findAllByCategoryUuid(category.getUuid())).thenReturn(bookCategoryList);
        doNothing().when(deleteCategoryPortOut).delete(category);

        // --- ACT
        useCase.delete(categoryUuid);

        // --- ASSERT
        verify(deleteCategoryPortOut).delete(category);
    }

    @Test
    @DisplayName("Deve retornar NotFoundException quando não encontrar a categoria")
    void shouldThrowNotFoundExceptionWhenCategoryIsNotFound() {
        // --- ARRANGE
        when(findCategoryPortOut.findByUuidOrThrow(categoryUuid)).thenThrow(new NotFoundException("Categoria não encontrada"));

        // --- ACT + ASSERT
        NotFoundException e = assertThrows(NotFoundException.class, () -> useCase.delete(categoryUuid));

        // --- ASSERT
        assertEquals("Categoria não encontrada", e.getMessage());
        verify(findBookCategoryPortOut, never()).findAllByCategoryUuid(any());
        verify(deleteBookCategoryPortOut, never()).deleteAll(any());
        verify(deleteCategoryPortOut, never()).delete(any());
    }
}
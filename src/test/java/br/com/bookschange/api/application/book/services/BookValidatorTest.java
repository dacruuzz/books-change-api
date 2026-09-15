package br.com.bookschange.api.application.book.services;

import br.com.bookschange.api.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookValidatorTest {

    @InjectMocks
    private BookValidator service;

    @Test
    @DisplayName("Deve validar a lista de categorias do livro")
    void shouldThrowNotFoundExceptionWhenCategoryListIsEmpty() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> service.validateCategories(List.of()));

        assertEquals("Categoria não encontrada", e.getMessage());
    }
}
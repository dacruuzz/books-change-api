package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.ports.out.DeleteBookPortOut;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.bookcategory.ports.out.DeleteBookCategoryPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.BookCategory;
import br.com.bookschange.api.domain.models.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBookUseCaseTest {

    @Mock private FindBookPortOut findBookPortOut;
    @Mock private DeleteBookPortOut deleteBookPortOut;
    @Mock private DeleteBookCategoryPortOut deleteBookCategoryPortOut;

    @InjectMocks
    private DeleteBookUseCase useCase;

    private Book book;
    private UUID bookUuid;

    @BeforeEach
    void setUp() {
        bookUuid = UUID.randomUUID();

        List<BookCategory> bookCategoryList = new ArrayList<>();
        bookCategoryList.add(mock(BookCategory.class));

        book = new Book();
        book.setUuid(bookUuid);
        book.setBookCategories(bookCategoryList);
    }

    @Test
    @DisplayName("Deve excluir um livro com bookCategories associados com sucesso")
    void shouldDeleteBookWithBookCategoriesSuccessfully() {
        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenReturn(book);
        assertEquals(bookUuid, book.getUuid());
        doNothing().when(deleteBookCategoryPortOut).deleteAll(book.getBookCategories());
        doNothing().when(deleteBookPortOut).delete(book);

        useCase.delete(bookUuid);

        verify(findBookPortOut).findByUuidOrThrow(any());
        verify(deleteBookCategoryPortOut).deleteAll(anyList());
        verify(deleteBookPortOut).delete(any());
    }

    @Test
    @DisplayName("Deve excluir um livro sem bookCategories associados com sucesso")
    void shouldDeleteBookWithoutBookCategoriesSuccessfully() {
        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenReturn(book);
        assertEquals(bookUuid, book.getUuid());
        doNothing().when(deleteBookPortOut).delete(book);

        useCase.delete(bookUuid);

        verify(findBookPortOut).findByUuidOrThrow(any());
        verify(deleteBookPortOut).delete(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o livro não for encontrado polo uuid")
    void shouldThrowNotFoundExceptionWhenBookIsNotFoundByUuid() {
        when(findBookPortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Livro não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.delete(any()));

        verify(deleteBookCategoryPortOut, never()).deleteAll(anyList());
        verify(deleteBookPortOut, never()).delete(any());
    }
}
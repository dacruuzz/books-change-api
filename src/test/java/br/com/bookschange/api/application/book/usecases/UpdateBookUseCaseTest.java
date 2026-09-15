package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.UpdateBookRequest;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponse;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.book.services.BookNormalizer;
import br.com.bookschange.api.application.book.services.BookValidator;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.domain.enums.CurrentCondition;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBookUseCaseTest {

    @Mock private BookMapper mapper;
    @Mock private BookNormalizer normalizer;
    @Mock private BookValidator validator;
    @Mock private FindBookPortOut findBookPortOut;
    @Mock private FindCategoryPortOut findCategoryPortOut;
    @Mock private SaveBookPortOut saveBookPortOut;

    @InjectMocks
    private UpdateBookUseCase useCase;

    private Book book;
    private Category category;
    private UpdateBookRequest request;
    private UUID bookUuid;

    @BeforeEach
    void setUp() {
        bookUuid = UUID.randomUUID();

        book = new Book();
        book.setUuid(bookUuid);
        book.setName("NOME ANTIGO");

        category = new Category();
        category.setUuid(UUID.randomUUID());
        category.setLabel("FICÇÃO");

        request = new UpdateBookRequest(
                "DOM CASMURRO",
                "MACHADO DE ASSIS",
                "EDITORA X",
                "RESUMO",
                List.of(category.getUuid()),
                CurrentCondition.GOOD
        );
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    void shouldUpdateBookSuccessfully() {
        BookResponse expectedResponse = new BookResponse(
                bookUuid, "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X",
                "RESUMO", Collections.emptyList(), CurrentCondition.GOOD, null
        );

        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenReturn(book);
        when(findCategoryPortOut.findAllByUuids(request.categories())).thenReturn(List.of(category));
        when(saveBookPortOut.save(book)).thenReturn(book);
        when(mapper.entityToBookResponse(book)).thenReturn(expectedResponse);

        BookResponse response = useCase.update(bookUuid, request);

        assertEquals(expectedResponse, response);
        verify(findBookPortOut).findByUuidOrThrow(bookUuid);
        verify(validator).validateCategories(anyList());
        verify(mapper).updateBookFromRequest(request, book);
        verify(normalizer).normalizeData(book);
        verify(saveBookPortOut).save(book);
    }

    @Test
    @DisplayName("Deve substituir as categorias do livro ao atualizar")
    void shouldReplaceBookCategoriesWhenUpdating() {
        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenReturn(book);
        when(findCategoryPortOut.findAllByUuids(request.categories())).thenReturn(List.of(category));
        when(saveBookPortOut.save(book)).thenReturn(book);
        when(mapper.entityToBookResponse(book)).thenReturn(mock(BookResponse.class));

        useCase.update(bookUuid, request);

        assertEquals(1, book.getBookCategories().size());
        assertEquals(category.getUuid(), book.getBookCategories().get(0).getCategory().getUuid());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o livro não for encontrado polo uuid")
    void shouldThrowNotFoundExceptionWhenBookIsNotFoundByUuid() {
        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenThrow(new NotFoundException("Livro não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.update(bookUuid, request));

        verify(saveBookPortOut, never()).save(any());
        verify(normalizer, never()).normalizeData(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando nenhuma categoria for encontrada")
    void shouldThrowNotFoundExceptionWhenNoCategoriesAreFound() {
        when(findBookPortOut.findByUuidOrThrow(bookUuid)).thenReturn(book);
        when(findCategoryPortOut.findAllByUuids(request.categories())).thenReturn(Collections.emptyList());
        doThrow(new NotFoundException("Categoria não encontrada"))
                .when(validator).validateCategories(anyList());

        assertThrows(NotFoundException.class, () -> useCase.update(bookUuid, request));

        verify(mapper, never()).updateBookFromRequest(any(), any());
        verify(saveBookPortOut, never()).save(any());
    }
}
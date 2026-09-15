package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponse;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.out.FindPagedBooksPortOut;
import br.com.bookschange.api.domain.enums.CurrentCondition;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;
import br.com.bookschange.infrastructure.shared.pagination.PageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPagedBooksUseCaseTest {

    @Mock private BookMapper bookMapper;
    @Mock private PageMapper pageMapper;
    @Mock private FindPagedBooksPortOut findPagedBooksPortOut;

    @InjectMocks
    private FindPagedBooksUseCase useCase;

    private Book book;
    private BookResponse bookResponse;
    private int page;
    private int pageSize;

    @BeforeEach
    void setUp() {
        page = 1;
        pageSize = 10;

        book = new Book();
        book.setUuid(UUID.randomUUID());
        book.setName("DOM CASMURRO");

        bookResponse = new BookResponse(
                book.getUuid(), "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X",
                "RESUMO", Collections.emptyList(), CurrentCondition.GOOD, UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("Deve buscar livros ativos paginados com sucesso")
    void shouldFindActivePagedBooksSuccessfully() {
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        PageDTO<BookResponse> expectedPageDTO = new PageDTO<>(1, pageSize, 1, 1L, List.of(bookResponse));

        when(findPagedBooksPortOut.findAllActivePaged(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.findAllPaged(page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(findPagedBooksPortOut).findAllActivePaged(any(Pageable.class));
        verify(bookMapper).entityToBookResponse(book);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver livros ativos")
    void shouldReturnEmptyPageWhenNoActiveBooksAreFound() {
        Page<Book> emptyBookPage = new PageImpl<>(Collections.emptyList());
        PageDTO<BookResponse> expectedPageDTO = new PageDTO<>(1, pageSize, 0, 0L, Collections.emptyList());

        when(findPagedBooksPortOut.findAllActivePaged(any(Pageable.class))).thenReturn(emptyBookPage);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.findAllPaged(page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(bookMapper, never()).entityToBookResponse(any());
    }

    @Test
    @DisplayName("Deve buscar livros ativos paginados com múltiplos resultados")
    void shouldFindActivePagedBooksWithMultipleResults() {
        Book secondBook = new Book();
        secondBook.setUuid(UUID.randomUUID());
        secondBook.setName("MEMÓRIAS PÓSTUMAS DE BRÁS CUBAS");

        BookResponse secondBookResponse = new BookResponse(
                secondBook.getUuid(), "MEMÓRIAS PÓSTUMAS DE BRÁS CUBAS", "MACHADO DE ASSIS", "EDITORA X",
                "RESUMO", Collections.emptyList(), CurrentCondition.GOOD, UUID.randomUUID()
        );

        Page<Book> bookPage = new PageImpl<>(List.of(book, secondBook));
        PageDTO<BookResponse> expectedPageDTO = new PageDTO<>(1, pageSize, 1, 2L, List.of(bookResponse, secondBookResponse));

        when(findPagedBooksPortOut.findAllActivePaged(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(bookMapper.entityToBookResponse(secondBook)).thenReturn(secondBookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.findAllPaged(page, pageSize);

        assertEquals(expectedPageDTO, response);
        assertEquals(2, response.content().size());
        verify(bookMapper, times(2)).entityToBookResponse(any());
    }
}
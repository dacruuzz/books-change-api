package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequest;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponse;
import br.com.bookschange.api.application.book.dtos.BookFilter;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.user.ports.out.FilterUserBooksPagedPortOut;
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
class FilterUserBooksPagedUseCaseTest {

    @Mock private BookMapper bookMapper;
    @Mock private PageMapper pageMapper;
    @Mock private FilterUserBooksPagedPortOut filterUserBooksPagedPortOut;

    @InjectMocks
    FilterUserBooksPagedUseCase useCase;

    private UUID ownerUuid;
    private Book book;
    private BookResponse bookResponse;
    private PageDTO<BookResponse> expectedPageDTO;
    int page;
    int pageSize;

    @BeforeEach
    void setUp() {
        ownerUuid = UUID.randomUUID();
        page = 1;
        pageSize = 10;

        book = new Book();
        book.setUuid(UUID.randomUUID());
        book.setName("DOM CASMURRO");

        bookResponse = mock(BookResponse.class);
        expectedPageDTO = new PageDTO<>(1, pageSize, 1, 1L, List.of(bookResponse));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário sem nenhum filtro aplicado")
    void shouldFilterUserBooksWithoutFiltersSuccessfully() {
        FilterBookRequest request = new FilterBookRequest(null, null, null, null, null);
        BookFilter filter = new BookFilter(null, null, null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(bookMapper).filterBookRequestToBookFilter(request);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário com todos os filtros aplicados")
    void shouldFilterUserBooksWithAllFiltersSuccessfully() {
        List<UUID> categoriesUuids = List.of(UUID.randomUUID());

        FilterBookRequest request = new FilterBookRequest(
                "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X", categoriesUuids, CurrentCondition.GOOD
        );
        BookFilter filter = new BookFilter(
                "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X", categoriesUuids, CurrentCondition.GOOD
        );
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(bookMapper).filterBookRequestToBookFilter(request);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo nome")
    void shouldFilterUserBooksByNameOnlySuccessfully() {
        FilterBookRequest request = new FilterBookRequest("DOM CASMURRO", null, null, null, null);
        BookFilter filter = new BookFilter("DOM CASMURRO", null, null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo autor")
    void shouldFilterUserBooksByAuthorOnlySuccessfully() {
        FilterBookRequest request = new FilterBookRequest(null, "MACHADO DE ASSIS", null, null, null);
        BookFilter filter = new BookFilter(null, "MACHADO DE ASSIS", null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pela editora")
    void shouldFilterUserBooksByPublisherOnlySuccessfully() {
        FilterBookRequest request = new FilterBookRequest(null, null, "EDITORA X", null, null);
        BookFilter filter = new BookFilter(null, null, "EDITORA X", null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelas categorias")
    void shouldFilterUserBooksByCategoriesOnlySuccessfully() {
        List<UUID> categoriesUuids = List.of(UUID.randomUUID());

        FilterBookRequest request = new FilterBookRequest(null, null, null, categoriesUuids, null);
        BookFilter filter = new BookFilter(null, null, null, categoriesUuids, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo estado de conservação")
    void shouldFilterUserBooksByCurrentConditionOnlySuccessfully() {
        FilterBookRequest request = new FilterBookRequest(null, null, null, null, CurrentCondition.GOOD);
        BookFilter filter = new BookFilter(null, null, null, null, CurrentCondition.GOOD);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponse(book)).thenReturn(bookResponse);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum livro for encontrado")
    void shouldReturnEmptyPageWhenNoBooksAreFound() {
        FilterBookRequest request = new FilterBookRequest("INEXISTENTE", null, null, null, null);
        BookFilter filter = new BookFilter("INEXISTENTE", null, null, null, null);
        Page<Book> emptyBookPage = new PageImpl<>(Collections.emptyList());
        PageDTO<BookResponse> emptyPageDTO = new PageDTO<>(1, pageSize, 0, 0L, Collections.emptyList());

        when(bookMapper.filterBookRequestToBookFilter(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(emptyBookPage);
        when(pageMapper.<BookResponse>toPageDTO(any())).thenReturn(emptyPageDTO);

        PageDTO<BookResponse> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(emptyPageDTO, response);
        verify(bookMapper, never()).entityToBookResponse(any());
    }
}
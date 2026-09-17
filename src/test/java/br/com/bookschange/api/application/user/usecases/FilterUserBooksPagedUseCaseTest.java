package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
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
    private BookResponseDTO bookResponseDTO;
    private PageDTO<BookResponseDTO> expectedPageDTO;
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

        bookResponseDTO = mock(BookResponseDTO.class);
        expectedPageDTO = new PageDTO<>(1, pageSize, 1, 1L, List.of(bookResponseDTO));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário sem nenhum filtro aplicado")
    void shouldFilterUserBooksWithoutFiltersSuccessfully() {
        FilterBookRequestDTO request = new FilterBookRequestDTO(null, null, null, null, null);
        BookFilterDTO filter = new BookFilterDTO(null, null, null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(bookMapper).filterBookRequestToBookFilterDto(request);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário com todos os filtros aplicados")
    void shouldFilterUserBooksWithAllFiltersSuccessfully() {
        List<UUID> categoriesUuids = List.of(UUID.randomUUID());

        FilterBookRequestDTO request = new FilterBookRequestDTO(
                "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X", categoriesUuids, CurrentCondition.GOOD
        );
        BookFilterDTO filter = new BookFilterDTO(
                "DOM CASMURRO", "MACHADO DE ASSIS", "EDITORA X", categoriesUuids, CurrentCondition.GOOD
        );
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(bookMapper).filterBookRequestToBookFilterDto(request);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo nome")
    void shouldFilterUserBooksByNameOnlySuccessfully() {
        FilterBookRequestDTO request = new FilterBookRequestDTO("DOM CASMURRO", null, null, null, null);
        BookFilterDTO filter = new BookFilterDTO("DOM CASMURRO", null, null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo autor")
    void shouldFilterUserBooksByAuthorOnlySuccessfully() {
        FilterBookRequestDTO request = new FilterBookRequestDTO(null, "MACHADO DE ASSIS", null, null, null);
        BookFilterDTO filter = new BookFilterDTO(null, "MACHADO DE ASSIS", null, null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pela editora")
    void shouldFilterUserBooksByPublisherOnlySuccessfully() {
        FilterBookRequestDTO request = new FilterBookRequestDTO(null, null, "EDITORA X", null, null);
        BookFilterDTO filter = new BookFilterDTO(null, null, "EDITORA X", null, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelas categorias")
    void shouldFilterUserBooksByCategoriesOnlySuccessfully() {
        List<UUID> categoriesUuids = List.of(UUID.randomUUID());

        FilterBookRequestDTO request = new FilterBookRequestDTO(null, null, null, categoriesUuids, null);
        BookFilterDTO filter = new BookFilterDTO(null, null, null, categoriesUuids, null);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar livros do usuário apenas pelo estado de conservação")
    void shouldFilterUserBooksByCurrentConditionOnlySuccessfully() {
        FilterBookRequestDTO request = new FilterBookRequestDTO(null, null, null, null, CurrentCondition.GOOD);
        BookFilterDTO filter = new BookFilterDTO(null, null, null, null, CurrentCondition.GOOD);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.entityToBookResponseDto(book)).thenReturn(bookResponseDTO);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(expectedPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(expectedPageDTO, response);
        verify(filterUserBooksPagedPortOut).find(eq(ownerUuid), eq(filter), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum livro for encontrado")
    void shouldReturnEmptyPageWhenNoBooksAreFound() {
        FilterBookRequestDTO request = new FilterBookRequestDTO("INEXISTENTE", null, null, null, null);
        BookFilterDTO filter = new BookFilterDTO("INEXISTENTE", null, null, null, null);
        Page<Book> emptyBookPage = new PageImpl<>(Collections.emptyList());
        PageDTO<BookResponseDTO> emptyPageDTO = new PageDTO<>(1, pageSize, 0, 0L, Collections.emptyList());

        when(bookMapper.filterBookRequestToBookFilterDto(request)).thenReturn(filter);
        when(filterUserBooksPagedPortOut.find(eq(ownerUuid), eq(filter), any(Pageable.class))).thenReturn(emptyBookPage);
        when(pageMapper.<BookResponseDTO>toPageDTO(any())).thenReturn(emptyPageDTO);

        PageDTO<BookResponseDTO> response = useCase.filter(ownerUuid, request, page, pageSize);

        assertEquals(emptyPageDTO, response);
        verify(bookMapper, never()).entityToBookResponseDto(any());
    }
}
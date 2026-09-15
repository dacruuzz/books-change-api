package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindBookUseCaseTest {

    @Mock private BookMapper mapper;
    @Mock private FindBookPortOut findBookPortOut;

    @InjectMocks
    private FindBookUseCase useCase;

    private UUID uuid;
    private Book book;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        book = new Book();
        book.setUuid(uuid);
    }

    @Test
    @DisplayName("Deve buscar o livro com sucesso")
    void shouldFindBookByUuidSuccessfully() {
        when(findBookPortOut.findByUuidOrThrow(uuid)).thenReturn(book);
        assertEquals(uuid, book.getUuid());

        BookResponseDTO expectedResponse = mock(BookResponseDTO.class);
        when(mapper.entityToBookResponse(book)).thenReturn(expectedResponse);

        BookResponseDTO result = useCase.findByUuid(uuid);

        assertEquals(expectedResponse, result);
        verify(findBookPortOut, times(1)).findByUuidOrThrow(any());
        verify(mapper, times(1)).entityToBookResponse(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando não encontrar um livro pelo uuid")
    void shouldThrowNotFoundExceptionWhenBookIsNotFoundByUuid() {
        when(findBookPortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Livro não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.findByUuid(any()));

        verify(mapper, never()).entityToBookResponse(any());
    }
}
package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponse;
import br.com.bookschange.api.application.store.mappers.StoreMapper;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Store;
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
class FindStoreUseCaseTest {

    @Mock private StoreMapper mapper;
    @Mock private FindStorePortOut findStorePortOut;

    @InjectMocks
    private FindStoreUseCase useCase;

    @Test
    @DisplayName("Deve buscar uma loja com sucesso")
    void shouldFindStoreSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Store store = new Store();
        StoreResponse expectedResponse = mock(StoreResponse.class);

        when(findStorePortOut.findByUuidOrThrow(uuid)).thenReturn(store);
        when(mapper.entityToStoreResponse(store)).thenReturn(expectedResponse);

        StoreResponse result = useCase.findByUuid(uuid);

        assertEquals(expectedResponse, result);
        verify(findStorePortOut).findByUuidOrThrow(uuid);
        verify(mapper).entityToStoreResponse(store);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando a loja não é encontrada pelo uuid")
    void shouldThrowNotFoundExceptionWhenStoreWasNotFoundByUuid() {
        when(findStorePortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Loja não encontrada"));
        assertThrows(NotFoundException.class, () -> useCase.findByUuid(any()));
        verify(mapper, never()).entityToStoreResponse(any());
    }
}
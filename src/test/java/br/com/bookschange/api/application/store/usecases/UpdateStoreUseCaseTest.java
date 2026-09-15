package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.UpdateStoreRequest;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponse;
import br.com.bookschange.api.application.store.mappers.StoreMapper;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.store.services.StoreNormalizer;
import br.com.bookschange.api.application.store.services.StoreValidator;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Store;
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
class UpdateStoreUseCaseTest {

    @Mock private StoreMapper mapper;
    @Mock private StoreValidator validator;
    @Mock private StoreNormalizer normalizer;
    @Mock private SaveStorePortOut saveStorePortOut;
    @Mock private FindStorePortOut findStorePortOut;

    private UpdateStoreRequest request;
    private Store store;
    private UUID uuid;

    @InjectMocks
    private UpdateStoreUseCase useCase;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        request = new UpdateStoreRequest(
                "Store",
                "00 00000-0000",
                "store-test",
                "Description"
        );

        store = new Store();
        store.setName(request.name());
        store.setPhone(request.phone());
        store.setSlug(request.slug());
        store.setDescription(request.description());
    }

    @Test
    @DisplayName("Deve atualizar um loja com sucesso")
    void shouldUpdateStoreSuccessfully() {
        StoreResponse expectedResponse = mock(StoreResponse.class);

        doNothing().when(validator).validateUpdate(uuid, request.slug());
        when(findStorePortOut.findByUuidOrThrow(uuid)).thenReturn(store);
        doNothing().when(mapper).updateStoreRequestToEntity(request, store);
        doNothing().when(normalizer).normalizeData(store);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(mapper.entityToStoreResponse(store)).thenReturn(expectedResponse);

        StoreResponse result = useCase.update(uuid, request);

        assertEquals(expectedResponse, result);
        verify(validator).validateUpdate(uuid, request.slug());
        verify(findStorePortOut).findByUuidOrThrow(uuid);
        verify(mapper).updateStoreRequestToEntity(request, store);
        verify(normalizer).normalizeData(store);
        verify(saveStorePortOut).save(store);
        verify(mapper).entityToStoreResponse(store);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando a loja não é encontrada pelo uuid")
    void shouldThrowNotFoundExceptionWhenStoreWasNotFoundByUuid() {
        when(findStorePortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Loja não encontrada"));
        assertThrows(NotFoundException.class, () -> useCase.update(uuid, request));

        verify(mapper, never()).updateStoreRequestToEntity(any(), any());
        verify(normalizer, never()).normalizeData(any());
        verify(saveStorePortOut, never()).save(any());
        verify(mapper, never()).entityToStoreResponse(any());
    }
}
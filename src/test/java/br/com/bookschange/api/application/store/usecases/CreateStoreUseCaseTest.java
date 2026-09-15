package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.CreateStoreRequest;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponse;
import br.com.bookschange.api.application.store.mappers.StoreMapper;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.store.services.StoreNormalizer;
import br.com.bookschange.api.application.store.services.StoreValidator;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.domain.models.User;
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
class CreateStoreUseCaseTest {

    @Mock private StoreMapper mapper;
    @Mock private StoreValidator validator;
    @Mock private StoreNormalizer normalizer;
    @Mock private SaveStorePortOut saveStorePortOut;
    @Mock private FindUserPortOut findUserPortOut;
    @Mock private SaveUserPortOut saveUserPortOut;

    private CreateStoreRequest request;
    private UUID ownerUuid;
    private User owner;
    private Store store;

    @InjectMocks
    private CreateStoreUseCase useCase;

    @BeforeEach
    void setUp() {
        ownerUuid = UUID.randomUUID();

        owner = new User();
        owner.setUuid(ownerUuid);
        owner.setUserType(UserType.DEFAULT);

        request = new CreateStoreRequest(
                "Store",
                "00.000.000/0000-00",
                "store@email.com",
                "00 00000-0000",
                "store-test",
                "Description",
                ownerUuid
        );

        store = new Store();
        store.setName(request.name());
        store.setCnpj(request.cnpj());
        store.setCommercialEmail(request.commercialEmail());
        store.setPhone(request.phone());
        store.setSlug(request.slug());
        store.setDescription(request.description());
    }

    @Test
    @DisplayName("Deve criar uma loja com sucesso")
    void shouldCreateStoreSuccessfully() {
        StoreResponse expectedResponse = mock(StoreResponse.class);

        doNothing().when(validator).validateCreation(request.commercialEmail(), request.cnpj(), request.slug(), request.ownerUuid());
        when(findUserPortOut.findByUuidOrThrow(ownerUuid)).thenReturn(owner);
        when(mapper.createStoreRequestToEntity(request)).thenReturn(store);
        doNothing().when(normalizer).normalizeData(store);
        when(saveUserPortOut.save(owner)).thenReturn(owner);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(mapper.entityToStoreResponse(store)).thenReturn(expectedResponse);

        StoreResponse result = useCase.create(request);

        assertEquals(expectedResponse, result);

        assertEquals(UserType.STORE, owner.getUserType());
        verify(validator).validateCreation(request.commercialEmail(), request.cnpj(), request.slug(), request.ownerUuid());
        verify(findUserPortOut).findByUuidOrThrow(ownerUuid);
        verify(mapper).createStoreRequestToEntity(request);
        verify(normalizer).normalizeData(store);
        verify(saveUserPortOut).save(owner);
        verify(saveStorePortOut).save(store);
        verify(mapper).entityToStoreResponse(store);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o usuário não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenOwnerWasNotFoundByUuid() {
        when(findUserPortOut.findByUuidOrThrow(ownerUuid)).thenThrow(new NotFoundException("Usuário não encontrada"));

        assertThrows(NotFoundException.class, () -> useCase.create(request));

        assertEquals(UserType.DEFAULT, owner.getUserType());
        verify(mapper, never()).createStoreRequestToEntity(request);
        verify(normalizer, never()).normalizeData(store);
        verify(saveUserPortOut, never()).save(owner);
        verify(saveStorePortOut, never()).save(store);
        verify(mapper, never()).entityToStoreResponse(store);
    }
}
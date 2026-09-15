package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Address;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignStoreAddressUseCaseTest {

    @Mock private FindStorePortOut findStorePortOut;
    @Mock private SaveStorePortOut saveStorePortOut;
    @Mock private FindAddressPortOut findAddressPortOut;

    private UUID addresUuid;
    private UUID storeUuid;

    @InjectMocks
    AssignStoreAddressUseCase useCase;

    @BeforeEach
    void setUp() {
        addresUuid = UUID.randomUUID();
        storeUuid = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve atribuir um endereço à loja com sucesso")
    void shouldAssignAddressToStoreSuccessfully() {
        Address address = new Address();
        Store store = new Store();

        when(findStorePortOut.findByUuidOrThrow(storeUuid)).thenReturn(store);
        when(findAddressPortOut.findByUuidOrThrow(addresUuid)).thenReturn(address);
        when(saveStorePortOut.save(store)).thenReturn(store);

        useCase.assign(storeUuid, addresUuid);

        assertEquals(address, store.getAddress());
        verify(findStorePortOut).findByUuidOrThrow(storeUuid);
        verify(findAddressPortOut).findByUuidOrThrow(addresUuid);
        verify(saveStorePortOut).save(store);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando a loja não é encontrada pelo uuid")
    void shouldThrowNotFoundExceptionWhenStoreWasNotFoundByUuid() {
        when(findStorePortOut.findByUuidOrThrow(storeUuid)).thenThrow(new NotFoundException("Loja não encontrada"));
        assertThrows(NotFoundException.class, () -> useCase.assign(storeUuid, addresUuid));
        verify(findAddressPortOut, never()).findByUuidOrThrow(any());
        verify(saveStorePortOut, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o endereço não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenAddressWasNotFindByUuid() {
        when(findAddressPortOut.findByUuidOrThrow(addresUuid)).thenThrow(new NotFoundException("Endereço não encontrado"));
        assertThrows(NotFoundException.class, () -> useCase.assign(storeUuid, addresUuid));
        verify(saveStorePortOut, never()).save(any());
    }
}
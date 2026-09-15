package br.com.bookschange.api.application.store.services;

import br.com.bookschange.api.application.address.ports.out.DeleteAddressPortOut;
import br.com.bookschange.api.application.store.ports.out.DeleteStorePortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreDeletionServiceTest {

    @Mock private DeleteStorePortOut deleteStorePortOut;
    @Mock private SaveUserPortOut saveUserPortOut;
    @Mock private DeleteAddressPortOut deleteAddressPortOut;

    private Store store;
    private Address address;
    private User owner;

    @InjectMocks
    StoreDeletionService service;

    @BeforeEach
    void setUp() {
        address = new Address();
        store = new Store();
        owner = new User();

        owner.setUserType(UserType.STORE);

        store.setAddress(address);
        store.setOwner(owner);
    }

    @Test
    @DisplayName("Deve excluir uma loja com endereço com sucesso")
    void shouldDeleteStoreWithAddressSuccessfully() {
        doNothing().when(deleteAddressPortOut).delete(store.getAddress());
        when(saveUserPortOut.save(owner)).thenReturn(owner);
        doNothing().when(deleteStorePortOut).delete(store);

        assertDoesNotThrow(() -> service.delete(store));

        assertEquals(UserType.DEFAULT, owner.getUserType());
        verify(deleteAddressPortOut).delete(store.getAddress());
        verify(saveUserPortOut).save(owner);
        verify(deleteStorePortOut).delete(store);
    }

    @Test
    @DisplayName("Deve excluir uma loja sem endereço com sucesso")
    void shouldDeleteStoreWithoutAddressSuccessfully() {
        store.setAddress(null);

        when(saveUserPortOut.save(owner)).thenReturn(owner);
        doNothing().when(deleteStorePortOut).delete(store);

        assertDoesNotThrow(() -> service.delete(store));

        assertEquals(UserType.DEFAULT, owner.getUserType());
        verify(deleteAddressPortOut, never()).delete(any());
        verify(saveUserPortOut).save(owner);
        verify(deleteStorePortOut).delete(store);
    }


}
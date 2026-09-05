package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponse;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactiveActiveUserUseCaseTest {

    private static final String ACTIVE = "active";
    private static final String INACTIVE = "inactive";

    @Mock private UserMapper mapper;
    @Mock private FindUserPortOut findUserPortOut;
    @Mock private SaveUserPortOut saveUserPortOut;
    @Mock private FindBookPortOut findBookPortOut;
    @Mock private SaveBookPortOut saveBookPortOut;
    @Mock private FindStorePortOut findStorePortOut;
    @Mock private SaveStorePortOut saveStorePortOut;
    @Mock private SaveAddressPortOut saveAddressPortOut;

    private UUID userUuid;
    private User user;
    private List<Book> bookList;
    private Store store;
    private Address address;

    @InjectMocks
    private InactiveActiveUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        user = new User();
        user.setUuid(userUuid);

        Book book = mock(Book.class);
        bookList = new ArrayList<>();
        bookList.add(book);

        store = mock(Store.class);

        address = mock(Address.class);
    }

    @Test
    @DisplayName("Deve inativar um usuário com sucesso")
    void shouldInactivateUserSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(true);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(bookList);
        when(saveBookPortOut.saveAll(bookList)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, INACTIVE);

        assertEquals(expectedResponse, response);
        assertFalse(user.isActive());
        verify(findUserPortOut).findByUuidOrThrow(userUuid);
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut).saveAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut).save(address);
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
        verify(mapper).entityToUserResponse(user);
    }

    @Test
    @DisplayName("Deve ativar um usuário com sucesso")
    void shouldActivateUserSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(false);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(bookList);
        when(saveBookPortOut.saveAll(bookList)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, ACTIVE);

        assertEquals(expectedResponse, response);
        assertTrue(user.isActive());
        verify(findUserPortOut).findByUuidOrThrow(userUuid);
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut).saveAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut).save(address);
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
        verify(mapper).entityToUserResponse(user);
    }

    @Test
    @DisplayName("Deve inativar um usuário que possui apenas livros, sem loja")
    void shouldInactivateUserWithOnlyBooksSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(true);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(bookList);
        when(saveBookPortOut.saveAll(bookList)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.empty());
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, INACTIVE);

        assertEquals(expectedResponse, response);
        assertFalse(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut).saveAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut, never()).save(any());
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve ativar um usuário que possui apenas livros, sem loja")
    void shouldActivateUserWithOnlyBooksSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(false);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(bookList);
        when(saveBookPortOut.saveAll(bookList)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.empty());
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, ACTIVE);

        assertEquals(expectedResponse, response);
        assertTrue(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut).saveAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut, never()).save(any());
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve inativar um usuário que possui apenas loja com endereço associado")
    void shouldInactivateUserWithStoreWithAddressSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(true);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(Collections.emptyList());
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, INACTIVE);

        assertEquals(expectedResponse, response);
        assertFalse(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut).save(address);
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve ativar um usuário que possui apenas loja com endereço associado")
    void shouldActivateUserWithStoreWithAddressSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(false);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(Collections.emptyList());
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, ACTIVE);

        assertEquals(expectedResponse, response);
        assertTrue(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut).save(address);
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve inativar um usuário que possui apenas loja sem endereço associado")
    void shouldInactivateUserWithStoreWithoutAddressSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(true);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(Collections.emptyList());
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(null);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, INACTIVE);

        assertEquals(expectedResponse, response);
        assertFalse(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve ativar um usuário que possui apenas loja sem endereço associado")
    void shouldActivateUserWithStoreWithoutAddressSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);
        user.setActive(false);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(user.getUuid())).thenReturn(Collections.emptyList());
        when(findStorePortOut.findByOwnerUuid(user.getUuid())).thenReturn(Optional.of(store));
        when(store.getAddress()).thenReturn(null);
        when(saveStorePortOut.save(store)).thenReturn(store);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.inactiveActive(userUuid, ACTIVE);

        assertEquals(expectedResponse, response);
        assertTrue(user.isActive());
        verify(findBookPortOut).findAllByOwnerUuid(user.getUuid());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut).findByOwnerUuid(user.getUuid());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut).save(store);
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando um usuário já estiver inativo")
    void shouldThrowBusinessExceptionWhenUserIsAlreadyInactive() {
        user.setActive(false);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);

        BusinessException e = assertThrows(BusinessException.class, () -> useCase.inactiveActive(userUuid, INACTIVE));

        assertEquals("O usuário já está inativo", e.getMessage());
        verify(findUserPortOut).findByUuidOrThrow(any());
        verify(findBookPortOut, never()).findAllByOwnerUuid(any());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut, never()).findByOwnerUuid(any());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut, never()).save(any());
        verify(saveUserPortOut, never()).save(any());
        verify(mapper, never()).entityToUserResponse(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando um usuário já estiver ativo")
    void shouldThrowBusinessExceptionWhenUserIsAlreadyActive() {
        user.setActive(true);

        when(findUserPortOut.findByUuidOrThrow(userUuid)).thenReturn(user);

        BusinessException e = assertThrows(BusinessException.class, () -> useCase.inactiveActive(userUuid, ACTIVE));

        assertEquals("O usuário já está ativo", e.getMessage());
        verify(findUserPortOut).findByUuidOrThrow(any());
        verify(findBookPortOut, never()).findAllByOwnerUuid(any());
        verify(saveBookPortOut, never()).saveAll(anyList());
        verify(findStorePortOut, never()).findByOwnerUuid(any());
        verify(saveAddressPortOut, never()).save(any());
        verify(saveStorePortOut, never()).save(any());
        verify(saveUserPortOut, never()).save(any());
        verify(mapper, never()).entityToUserResponse(any());
    }
}
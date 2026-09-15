package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.book.ports.out.DeleteBookPortOut;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.services.StoreDeletionService;
import br.com.bookschange.api.application.user.ports.out.DeleteUserPortOut;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock private FindUserPortOut findUserPortOut;
    @Mock private DeleteUserPortOut deleteUserPortOut;
    @Mock private FindBookPortOut findBookPortOut;
    @Mock private DeleteBookPortOut deleteBookPortOut;
    @Mock private FindStorePortOut findStorePortOut;
    @Mock private StoreDeletionService storeDeletionService;

    private UUID uuid;
    private User user;
    private Store store;
    private List<Book> bookList;

    @InjectMocks
    private DeleteUserUseCase useCase;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        user = new User();
        user.setUuid(uuid);

        Book book = mock(Book.class);
        store = mock(Store.class);

        bookList = new ArrayList<>();
        bookList.add(book);
    }

    @Test
    @DisplayName("Deve excluir um usuário com livros e loja com sucesso")
    void shouldDeleteUserWithBooksAndStoreSuccessfully() {
        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(uuid)).thenReturn(bookList);
        doNothing().when(deleteBookPortOut).deleteAll(bookList);
        when(findStorePortOut.findByOwnerUuid(uuid)).thenReturn(Optional.of(store));
        doNothing().when(storeDeletionService).delete(store);
        doNothing().when(deleteUserPortOut).delete(user);

        useCase.delete(uuid);

        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(findBookPortOut).findAllByOwnerUuid(uuid);
        verify(deleteBookPortOut).deleteAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(uuid);
        verify(storeDeletionService).delete(store);
        verify(deleteUserPortOut).delete(user);
    }

    @Test
    @DisplayName("Deve excluir um usuário com apenas livros com sucesso")
    void shouldDeleteUserWithOnlyBooksSuccessfully() {
        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(uuid)).thenReturn(bookList);
        doNothing().when(deleteBookPortOut).deleteAll(bookList);
        when(findStorePortOut.findByOwnerUuid(uuid)).thenReturn(Optional.empty());
        doNothing().when(deleteUserPortOut).delete(user);

        useCase.delete(uuid);

        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(findBookPortOut).findAllByOwnerUuid(uuid);
        verify(deleteBookPortOut).deleteAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(uuid);
        verify(storeDeletionService, never()).delete(store);
        verify(deleteUserPortOut).delete(user);
    }

    @Test
    @DisplayName("Deve excluir um usuário com apenas loja com sucesso")
    void shouldDeleteUserWithOnlyStoreSuccessfully() {
        bookList.clear();

        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(uuid)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(uuid)).thenReturn(Optional.of(store));
        doNothing().when(storeDeletionService).delete(store);
        doNothing().when(deleteUserPortOut).delete(user);

        useCase.delete(uuid);

        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(findBookPortOut).findAllByOwnerUuid(uuid);
        verify(deleteBookPortOut, never()).deleteAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(uuid);
        verify(storeDeletionService).delete(store);
        verify(deleteUserPortOut).delete(user);
    }

    @Test
    @DisplayName("Deve excluir um usuário sem livros e loja com sucesso")
    void shouldDeleteUserWithOutBooksAndStoreSuccessfully() {
        bookList.clear();

        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        when(findBookPortOut.findAllByOwnerUuid(uuid)).thenReturn(bookList);
        when(findStorePortOut.findByOwnerUuid(uuid)).thenReturn(Optional.empty());
        doNothing().when(deleteUserPortOut).delete(user);

        useCase.delete(uuid);

        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(findBookPortOut).findAllByOwnerUuid(uuid);
        verify(deleteBookPortOut, never()).deleteAll(bookList);
        verify(findStorePortOut).findByOwnerUuid(uuid);
        verify(storeDeletionService, never()).delete(store);
        verify(deleteUserPortOut).delete(user);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o usuário não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenUserWasNotFindByUuid() {
        when(findUserPortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Usuário não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.delete(uuid));

        verify(findBookPortOut, never()).findAllByOwnerUuid(any());
        verify(deleteBookPortOut, never()).deleteAll(anyList());
        verify(findStorePortOut, never()).findByOwnerUuid(any());
        verify(storeDeletionService, never()).delete(any());
        verify(deleteUserPortOut, never()).delete(any());
    }
}
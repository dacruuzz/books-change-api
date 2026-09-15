package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.services.StoreDeletionService;
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
class DeleteStoreUseCaseTest {

    @Mock private FindStorePortOut findStorePortOut;
    @Mock private StoreDeletionService storeDeletionService;

    @InjectMocks
    private DeleteStoreUseCase useCase;

    @Test
    @DisplayName("Deve excluir uma loja com sucesso")
    void shouldDeleteStoreSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Store store = new Store();

        when(findStorePortOut.findByUuidOrThrow(uuid)).thenReturn(store);
        doNothing().when(storeDeletionService).delete(store);

        useCase.delete(uuid);

        verify(findStorePortOut).findByUuidOrThrow(uuid);
        verify(storeDeletionService).delete(store);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando a loja não é encontrada pelo uuid")
    void shouldThrowNotFoundExceptionWhenStoreWasNotFoundByUuid() {
        when(findStorePortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Loja não encontrada"));
        assertThrows(NotFoundException.class, () -> useCase.delete(any()));
        verify(storeDeletionService, never()).delete(any());
    }
}
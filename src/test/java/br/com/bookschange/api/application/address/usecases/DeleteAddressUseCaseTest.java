package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.ports.out.DeleteAddressPortOut;
import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Address;
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
class DeleteAddressUseCaseTest {

    @Mock private FindAddressPortOut findAddressPortOut;
    @Mock private DeleteAddressPortOut deleteAddressPortOut;

    @InjectMocks
    private DeleteAddressUseCase useCase;

    @Test
    @DisplayName("Deve excluir um endereço com sucesso")
    void shouldDeleteAddressSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Address address = new Address();

        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenReturn(address);
        doNothing().when(deleteAddressPortOut).delete(address);

        useCase.delete(uuid);

        verify(findAddressPortOut, times(1)).findByUuidOrThrow(any());
        verify(deleteAddressPortOut, times(1)).delete(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o endereço não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenAddressWasNotFindByUuid() {
        when(findAddressPortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Endereço não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.delete(any()));

        verify(deleteAddressPortOut, never()).delete(any());
    }
}
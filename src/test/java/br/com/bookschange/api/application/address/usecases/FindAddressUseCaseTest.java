package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponse;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Address;
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
class FindAddressUseCaseTest {

    @Mock private AddressMapper mapper;
    @Mock private FindAddressPortOut findAddressPortOut;

    @InjectMocks
    private FindAddressUseCase useCase;

    private UUID uuid;
    private Address address;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        address = new Address();
    }

    @Test
    @DisplayName("Deve buscar um endereço pelo uuid com sucesso")
    void shouldFindAddressByUuidSuccessfully() {
        AddressResponse expectedResponse = mock(AddressResponse.class);

        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenReturn(address);
        when(mapper.entityToAddressResponse(address)).thenReturn(expectedResponse);

        AddressResponse result = useCase.findByUuid(uuid);

        assertEquals(expectedResponse, result);
        verify(findAddressPortOut).findByUuidOrThrow(uuid);
        verify(mapper).entityToAddressResponse(address);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException qunado o endereço não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenAddressWasNotFindByUuid() {
        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Endereço não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.findByUuid(uuid));

        verify(findAddressPortOut).findByUuidOrThrow(uuid);
        verify(mapper, never()).entityToAddressResponse(any());
    }
}
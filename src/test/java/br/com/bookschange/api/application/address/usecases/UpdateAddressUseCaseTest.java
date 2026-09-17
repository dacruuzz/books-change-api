package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.UpdateAddressRequestDTO;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.address.services.normalizers.AddressNormalizer;
import br.com.bookschange.api.application.address.services.validators.AddressValidator;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAddressUseCaseTest {

    @Mock private AddressMapper mapper;
    @Mock private AddressNormalizer normalizer;
    @Mock private AddressValidator validator;
    @Mock private FindAddressPortOut findAddressPortOut;
    @Mock private SaveAddressPortOut saveAddressPortOut;

    @InjectMocks
    private UpdateAddressUseCase useCase;

    private UUID uuid;
    private Address address;
    private UpdateAddressRequestDTO request;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        address = new Address();
        request = new UpdateAddressRequestDTO(
                "70680-642",
                "street",
                "000",
                "state",
                "country",
                "city",
                "complement",
                "neighborhood"
        );

        address.setZipCode(request.zipCode());
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
    }

    @Test
    @DisplayName("Deve atualizar um endereço com sucesso")
    void shouldUpdateAddressSuccessfully() {
        AddressResponseDTO expectedResponse = mock(AddressResponseDTO.class);

        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenReturn(address);
        doNothing().when(validator).validateZipCode(request.zipCode());
        doNothing().when(mapper).updateAddressRequestDtoToEntity(request, address);
        doNothing().when(normalizer).normalizeData(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(mapper.entityToAddressResponseDto(address)).thenReturn(expectedResponse);

        AddressResponseDTO result = useCase.update(uuid, request);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

        assertEquals(expectedResponse, result);
        verify(findAddressPortOut).findByUuidOrThrow(uuid);
        verify(validator).validateZipCode(request.zipCode());
        verify(mapper).updateAddressRequestDtoToEntity(request, address);
        verify(normalizer).normalizeData(address);
        verify(saveAddressPortOut).save(addressCaptor.capture());
        verify(mapper).entityToAddressResponseDto(address);
    }

    @Test
    @DisplayName("Deve atualizar um endereço quando todos os campos do request forem nulls")
    void shouldUpdateAddressWhenAllRequestFieldsIsNull() {
        UpdateAddressRequestDTO requestNull = new UpdateAddressRequestDTO(null, null, null,null,null,null,null, null);
        AddressResponseDTO expectedResponse = mock(AddressResponseDTO.class);

        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenReturn(address);
        doNothing().when(mapper).updateAddressRequestDtoToEntity(requestNull, address);
        doNothing().when(normalizer).normalizeData(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);
        when(mapper.entityToAddressResponseDto(address)).thenReturn(expectedResponse);

        AddressResponseDTO result = useCase.update(uuid, requestNull);
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);

        assertEquals(expectedResponse, result);
        verify(findAddressPortOut).findByUuidOrThrow(uuid);
        verify(mapper).updateAddressRequestDtoToEntity(requestNull, address);
        verify(normalizer).normalizeData(address);
        verify(saveAddressPortOut).save(addressCaptor.capture());
        verify(mapper).entityToAddressResponseDto(address);
        verify(validator, never()).validateZipCode(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException qunado o endereço não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenAddressWasNotFindByUuid() {
        when(findAddressPortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Endereço não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.update(uuid, request));

        verify(findAddressPortOut).findByUuidOrThrow(uuid);
        verify(validator, never()).validateZipCode(any());
        verify(mapper, never()).updateAddressRequestDtoToEntity(any(), any());
        verify(normalizer, never()).normalizeData(any());
        verify(saveAddressPortOut, never()).save(any());
        verify(mapper, never()).entityToAddressResponseDto(any());
    }
}
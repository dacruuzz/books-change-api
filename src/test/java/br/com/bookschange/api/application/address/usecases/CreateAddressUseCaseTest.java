package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.CreateAddressRequest;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponse;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.address.services.AddressNormalizer;
import br.com.bookschange.api.application.address.services.AddressValidator;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAddressUseCaseTest {

    @Mock private AddressMapper mapper;
    @Mock private AddressNormalizer normalizer;
    @Mock private AddressValidator validator;
    @Mock private SaveAddressPortOut saveAddressPortOut;

    @InjectMocks
    private CreateAddressUseCase useCase;

    private CreateAddressRequest request;
    private Address address;

    @BeforeEach
    void setUp() {
        request = new CreateAddressRequest(
                "70680-642",
                "street",
                "000",
                "state",
                "country",
                "city",
                "complement",
                "neighborhood"
        );

        address = new Address();
        address.setZipCode(request.zipCode());
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
    }

    @Test
    @DisplayName("Deve criar um endereço com sucesso")
    void shouldCreateNewAddressSuccessfully() {
        doNothing().when(validator).validateZipCode(request.zipCode());
        when(mapper.createAddressRequestToEntity(request)).thenReturn(address);
        doNothing().when(normalizer).normalizeData(address);
        when(saveAddressPortOut.save(address)).thenReturn(address);

        AddressResponse expectedResponse = mock(AddressResponse.class);
        when(mapper.entityToAddressResponse(address)).thenReturn(expectedResponse);

        AddressResponse result = useCase.create(request);

        assertEquals(expectedResponse, result);

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(saveAddressPortOut).save(addressCaptor.capture());

        verify(normalizer, times(1)).normalizeData(any());
        verify(validator, times(1)).validateZipCode(anyString());
        verify(mapper, times(1)).createAddressRequestToEntity(any());
        verify(mapper, times(1)).entityToAddressResponse(any());
        verify(saveAddressPortOut).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o cep estiver incorreto")
    void shouldThrowBusinessExceptionWhenZipCodeIsIncorrect() {
        doThrow(new BusinessException("O cep deve conter os 8 dígitos")).when(validator).validateZipCode(anyString());

        assertThrows(BusinessException.class, () -> useCase.create(request));

        verify(normalizer, never()).normalizeData(any());
        verify(mapper, never()).createAddressRequestToEntity(any());
        verify(mapper, never()).entityToAddressResponse(any());
        verify(saveAddressPortOut, never()).save(any());
    }
}
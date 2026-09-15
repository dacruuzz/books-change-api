package br.com.bookschange.api.application.address.services;

import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AddressNormalizerTest {

    private AddressNormalizer service;

    private Address address;

    @BeforeEach
    void setUp() {
        service = new AddressNormalizer(new TextNormalizer());

        address = new Address();
        address.setZipCode("70680-642");
        address.setStreet("SQSW 304 Bloco A");
        address.setNumber("101b");
        address.setState("Distrito Federal");
        address.setCountry("Brasil");
        address.setCity("Brasília");
        address.setComplement("Apartamento 302");
        address.setNeighborhood("Sudoeste");
    }

    @Test
    @DisplayName("Deve normalizar todos os campos de endereço com sucesso")
    void shouldNormalizeAllAddressFieldsSuccessfully() {
        service.normalizeData(address);

        assertEquals("70680642", address.getZipCode());
        assertEquals("SQSW 304 BLOCO A", address.getStreet());
        assertEquals("101B", address.getNumber());
        assertEquals("DISTRITO FEDERAL", address.getState());
        assertEquals("BRASIL", address.getCountry());
        assertEquals("BRASÍLIA", address.getCity());
        assertEquals("APARTAMENTO 302", address.getComplement());
        assertEquals("SUDOESTE", address.getNeighborhood());
    }
}
package br.com.bookschange.api.application.address.services;

import br.com.bookschange.api.domain.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddressValidatorTest {

    private final AddressValidator validator = new AddressValidator();

    @Test
    @DisplayName("Deve aceitar o cep válido")
    void shouldAcceptValidZipCode() {
        assertDoesNotThrow(() -> validator.validateZipCode("70680-642"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o cep possui menos que 8 dígitos")
    void shouldThrowBusinessExceptionWhenZipCodeHasLessThan8Digits() {
        BusinessException e = assertThrows(
                BusinessException.class,
                () -> validator.validateZipCode("70680-64")
        );

        assertEquals("O cep deve conter os 8 dígitos", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o cep possui mais que 8 dígitos")
    void shouldThrowBusinessExceptionWhenZipCodeHasMoreThan8Digits() {
        BusinessException e = assertThrows(
                BusinessException.class,
                () -> validator.validateZipCode("70680-6421")
        );

        assertEquals("O cep deve conter os 8 dígitos", e.getMessage());
    }
}
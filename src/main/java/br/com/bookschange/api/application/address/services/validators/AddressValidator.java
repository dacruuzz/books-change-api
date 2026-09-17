package br.com.bookschange.api.application.address.services.validators;

import br.com.bookschange.api.domain.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddressValidator {

    public void validateZipCode(String zipCode) {
        String normalizedZipCode = zipCode.replaceAll("\\D", "");

        if (normalizedZipCode.length() != 8) {
            log.warn("O cep deve conter os 8 dígitos");
            throw new BusinessException("O cep deve conter os 8 dígitos");
        }
    }
}

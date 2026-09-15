package br.com.bookschange.api.application.address.services.normalizers;

import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.shared.services.validators.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressNormalizer {

    private final TextNormalizer normalizer;

    public void normalizeData(Address address) {
        address.setZipCode(normalizer.normalizeZipCode(address.getZipCode()));
        address.setStreet(normalizer.normalizeToUpperCase(address.getStreet()));
        address.setNumber(normalizer.normalizeToUpperCase(address.getNumber()));
        address.setState(normalizer.normalizeToUpperCase(address.getState()));
        address.setCountry(normalizer.normalizeToUpperCase(address.getCountry()));
        address.setCity(normalizer.normalizeToUpperCase(address.getCity()));
        address.setComplement(normalizer.normalizeToUpperCase(address.getComplement()));
        address.setNeighborhood(normalizer.normalizeToUpperCase(address.getNeighborhood()));
    }
}

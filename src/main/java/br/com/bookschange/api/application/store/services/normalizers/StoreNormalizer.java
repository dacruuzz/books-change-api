package br.com.bookschange.api.application.store.services.normalizers;

import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.shared.services.validators.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreNormalizer {

    private final TextNormalizer normalizer;

    public void normalizeData(Store store) {
        store.setName(normalizer.normalizeToUpperCase(store.getName()));
        store.setCnpj(normalizer.normalizeCnpj(store.getCnpj()));
        store.setCommercialEmail(normalizer.normalizeEmail(store.getCommercialEmail()));
        store.setPhone(normalizer.normalizePhone(store.getPhone()));
        store.setSlug(normalizer.normalizeToLowerCase(store.getSlug()));
        store.setDescription(normalizer.normalizeToUpperCase(store.getDescription()));
    }
}
package br.com.bookschange.api.shared.services.validators;

import br.com.bookschange.infrastructure.shared.util.CNPJUtil;
import br.com.bookschange.infrastructure.shared.util.CPFUtil;
import br.com.bookschange.infrastructure.shared.util.PhoneUtil;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TextNormalizer {

    private String normalize(String value) {
        if (value == null) return null;

        value = value.trim();

        return value.isEmpty() ? null : value;
    }

    public String normalizeToUpperCase(String value) {
        value = normalize(value);

        return value == null ? null : value.toUpperCase(Locale.ROOT);
    }

    public String normalizeToLowerCase(String value) {
        value = normalize(value);

        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    public String normalizeEmail(String value) {
        value = normalize(value);

        return value == null ? null : normalizeToUpperCase(value);
    }

    public String normalizeCpf(String value) {
        value = normalize(value);

        return value == null ? null : CPFUtil.normalize(value);
    }

    public String normalizeCnpj(String value) {
        value = normalize(value);

        return value == null ? null : CNPJUtil.normalize(value);
    }

    public String normalizePhone(String value) {
        value = normalize(value);

        return value == null ? null : PhoneUtil.normalize(value);
    }

    public String normalizeZipCode(String value) {
        value = normalize(value);

        return value == null ? null : value.replaceAll("\\D", "");
    }

    public String capitalize(String value) {
        value = normalize(value);

        if (value == null) return null;

        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT);
    }
}

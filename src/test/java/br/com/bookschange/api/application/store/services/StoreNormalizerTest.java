package br.com.bookschange.api.application.store.services;

import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreNormalizerTest {

    @Mock private TextNormalizer textNormalizer;

    @InjectMocks
    private StoreNormalizer storeNormalizer;

    private final String NORMALIZED_BOOK = "BOOK";
    private final String NORMALIZED_CNPJ = "00000000000000";
    private final String NORMALIZED_EMAIL = "TEST@EMAIL.COM";
    private final String NORMALIZED_PHONE = "00000000000";
    private final String NORMALIZED_SLUG = "slug";
    private final String NORMALIZED_DESCRIPTION = "DESCRIPTION";

    @Test
    @DisplayName("Deve normalizar todos os campos da loja com sucesso")
    void shouldNormalizeAllStoreFieldsSuccessfully() {
        Store store = new Store();
        store.setName("Book");
        store.setCnpj("00.000.000/0000-00");
        store.setCommercialEmail("test@email.com");
        store.setPhone("00 00000-0000");
        store.setSlug("Slug");
        store.setDescription("Description");

        when(textNormalizer.normalizeToUpperCase(store.getName())).thenReturn(NORMALIZED_BOOK);
        when(textNormalizer.normalizeCnpj(store.getCnpj())).thenReturn(NORMALIZED_CNPJ);
        when(textNormalizer.normalizeEmail(store.getCommercialEmail())).thenReturn(NORMALIZED_EMAIL);
        when(textNormalizer.normalizePhone(store.getPhone())).thenReturn(NORMALIZED_PHONE);
        when(textNormalizer.normalizeToLowerCase(store.getSlug())).thenReturn(NORMALIZED_SLUG);
        when(textNormalizer.normalizeToUpperCase(store.getDescription())).thenReturn(NORMALIZED_DESCRIPTION);

        assertDoesNotThrow(() -> storeNormalizer.normalizeData(store));

        verify(textNormalizer, times(2)).normalizeToUpperCase(anyString());
        verify(textNormalizer).normalizeCnpj(anyString());
        verify(textNormalizer).normalizeEmail(anyString());
        verify(textNormalizer).normalizePhone(anyString());
        verify(textNormalizer).normalizeToLowerCase(anyString());
    }
}
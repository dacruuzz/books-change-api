package br.com.bookschange.api.application.store.services;

import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreValidatorTest {

    @Mock private FindStorePortOut findStorePortOut;

    private final String NORMALIZED_EMAIL = "TESTE@EMAIL.COM";
    private final String NORMALIZED_CNPJ = "00000000000000";
    private final String NORMALIZED_SLUG = "slug-teste";

    private StoreValidator validator;
    private UUID ownerUuid;
    private Store store;

    @BeforeEach
    void setUp() {
        validator = new StoreValidator(new TextNormalizer(), findStorePortOut);
        ownerUuid = UUID.randomUUID();
        store = new Store();
        store.setCommercialEmail("teste@email.com");
        store.setCnpj("00.000.000/0000-00");
        store.setSlug("Slug-Teste");
    }

    @Test
    @DisplayName("Deve validar criação com sucesso")
    void shouldValidateCreationSuccessfully() {
        when(findStorePortOut.existsByEmail(NORMALIZED_EMAIL)).thenReturn(false);
        when(findStorePortOut.existsByCnpj(NORMALIZED_CNPJ)).thenReturn(false);
        when(findStorePortOut.existsBySlug(NORMALIZED_SLUG)).thenReturn(false);
        when(findStorePortOut.findByOwnerUuid(ownerUuid)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateCreation(
                store.getCommercialEmail(),
                store.getCnpj(),
                store.getSlug(),
                ownerUuid
        ));

        verify(findStorePortOut).existsByEmail(NORMALIZED_EMAIL);
        verify(findStorePortOut).existsByCnpj(NORMALIZED_CNPJ);
        verify(findStorePortOut).existsBySlug(NORMALIZED_SLUG);
        verify(findStorePortOut).findByOwnerUuid(ownerUuid);
    }

    @Test
    @DisplayName("Deve validar atualização com sucesso")
    void shouldValidateUpdateSuccessfully() {
        UUID uuid = UUID.randomUUID();

        when(findStorePortOut.findBySlug(NORMALIZED_SLUG)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> validator.validateUpdate(uuid, NORMALIZED_SLUG));
        verify(findStorePortOut).findBySlug(NORMALIZED_SLUG);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o email já existe")
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        when(findStorePortOut.existsByEmail(NORMALIZED_EMAIL)).thenReturn(true);
        BusinessException e = assertThrows(BusinessException.class, () -> validator.validateEmail(NORMALIZED_EMAIL));
        assertEquals("Já existe uma loja cadastrada com esse e-mail", e.getMessage());
        verify(findStorePortOut).existsByEmail(NORMALIZED_EMAIL);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o CNPJ já existe")
    void shouldThrowBusinessExceptionWhenCNPJAlreadyExists() {
        when(findStorePortOut.existsByCnpj(NORMALIZED_CNPJ)).thenReturn(true);
        BusinessException e = assertThrows(BusinessException.class, () -> validator.validateCnpj(NORMALIZED_CNPJ));
        assertEquals("Já existe uma loja cadastrada com esse CNPJ", e.getMessage());
        verify(findStorePortOut).existsByCnpj(NORMALIZED_CNPJ);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o identificador já existe")
    void shouldThrowBusinessExceptionWhenSlugAlreadyExists() {
        when(findStorePortOut.existsBySlug(NORMALIZED_SLUG)).thenReturn(true);
        BusinessException e = assertThrows(BusinessException.class, () -> validator.validateSlug(NORMALIZED_SLUG));
        assertEquals("Já existe uma loja cadastrada com esse identificador", e.getMessage());
        verify(findStorePortOut).existsBySlug(NORMALIZED_SLUG);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o usuário já possui um loja")
    void shouldThrowBusinessExceptionWhenOwnerAlreadyHasStore() {
        when(findStorePortOut.findByOwnerUuid(ownerUuid)).thenReturn(Optional.of(store));
        BusinessException e = assertThrows(BusinessException.class, () -> validator.validateOwner(ownerUuid));
        assertEquals("O usuário já possui uma loja", e.getMessage());
    }
}
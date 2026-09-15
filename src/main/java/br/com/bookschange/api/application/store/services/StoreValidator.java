package br.com.bookschange.api.application.store.services;

import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.shared.services.TextNormalizer;
import br.com.bookschange.infrastructure.shared.util.CNPJUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreValidator {

    private final TextNormalizer normalizer;
    private final FindStorePortOut findStorePortOut;

    public void validateEmail(String email) {
        String normalizedEmail = normalizer.normalizeEmail(email);

        if (findStorePortOut.existsByEmail(normalizedEmail)) {
            log.warn("Tentativa de cadastro com e-mail existente");
            throw new BusinessException("Já existe uma loja cadastrada com esse e-mail");
        }
    }

    public void validateCnpj(String cnpj) {
        String normalizedCnpj = CNPJUtil.normalize(cnpj);

        if (findStorePortOut.existsByCnpj(normalizedCnpj)) {
            log.warn("Tentativa de cadastro com CNPJ existente");
            throw new BusinessException("Já existe uma loja cadastrada com esse CNPJ");
        }
    }

    public void validateSlug(String slug) {
        String normalizedSlug = normalizer.normalizeToLowerCase(slug);

        if (findStorePortOut.existsBySlug(normalizedSlug)) {
            log.warn("Tentativa de cadastro com identificador existente");
            throw new BusinessException("Já existe uma loja cadastrada com esse identificador");
        }
    }

    public void validateOwner(UUID ownerUuid) {
        Optional<Store> store = findStorePortOut.findByOwnerUuid(ownerUuid);

        if (store.isPresent()) {
            log.warn("O usuário fornecido já possui uma loja | ownerUuid: {}", ownerUuid);
            throw new BusinessException("O usuário já possui uma loja");
        }
    }

    public void validateCreation(String email, String cnpj, String slug, UUID ownerUuid) {
        validateEmail(email);
        validateCnpj(cnpj);
        validateSlug(slug);
        validateOwner(ownerUuid);
    }

    public void validateUpdate(UUID uuid, String slug) {
        String normalizedSlug = normalizer.normalizeToLowerCase(slug);

        findStorePortOut.findBySlug(normalizedSlug)
                .ifPresent(store -> {
                    if (!store.getUuid().equals(uuid)) {
                        log.warn("Tentativa de edição utilizando identificador já existente | uuid: {}", uuid);
                        throw new BusinessException("Já existe uma loja cadastrada com esse identificador");
                    }
                });
    }
}
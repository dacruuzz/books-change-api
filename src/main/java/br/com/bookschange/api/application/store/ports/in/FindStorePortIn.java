package br.com.bookschange.api.application.store.ports.in;

import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponseDTO;

import java.util.UUID;

public interface FindStorePortIn {
    StoreResponseDTO findByUuid(UUID uuid);
}

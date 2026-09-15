package br.com.bookschange.api.application.store.ports.in;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.UpdateStoreRequestDTO;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponseDTO;

import java.util.UUID;

public interface UpdateStorePortIn {
    StoreResponseDTO update(UUID uuid, UpdateStoreRequestDTO request);
}

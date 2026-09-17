package br.com.bookschange.api.application.store.ports.in;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.CreateStoreRequestDTO;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponseDTO;

public interface CreateStorePortIn {
    StoreResponseDTO create(CreateStoreRequestDTO request);
}

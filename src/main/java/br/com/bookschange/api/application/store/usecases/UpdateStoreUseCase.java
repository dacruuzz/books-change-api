package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.UpdateStoreRequestDTO;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponseDTO;
import br.com.bookschange.api.application.store.mappers.StoreMapper;
import br.com.bookschange.api.application.store.ports.in.UpdateStorePortIn;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.store.services.StoreNormalizer;
import br.com.bookschange.api.application.store.services.StoreValidator;
import br.com.bookschange.api.domain.models.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateStoreUseCase implements UpdateStorePortIn {

    private final StoreMapper mapper;
    private final StoreValidator validator;
    private final StoreNormalizer normalizer;
    private final SaveStorePortOut saveStorePortOut;
    private final FindStorePortOut findStorePortOut;

    @Override
    public StoreResponseDTO update(UUID uuid, UpdateStoreRequestDTO request) {
        log.info("Buscando loja para edição | uuid: {}", uuid);

        validator.validateUpdate(uuid, request.slug());

        Store store = findStorePortOut.findByUuidOrThrow(uuid);

        mapper.updateStoreRequestDtoToEntity(request, store);

        normalizer.normalizeData(store);

        Store updatedStore = saveStorePortOut.save(store);

        log.info("Edição de loja feita com sucesso");
        return mapper.entityToStoreResponseDto(updatedStore);
    }
}

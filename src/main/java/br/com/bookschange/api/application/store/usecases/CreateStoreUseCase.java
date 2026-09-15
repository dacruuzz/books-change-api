package br.com.bookschange.api.application.store.usecases;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.CreateStoreRequest;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponse;
import br.com.bookschange.api.application.store.mappers.StoreMapper;
import br.com.bookschange.api.application.store.ports.in.CreateStorePortIn;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.store.services.StoreNormalizer;
import br.com.bookschange.api.application.store.services.StoreValidator;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.domain.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateStoreUseCase implements CreateStorePortIn {

    private final StoreMapper mapper;
    private final StoreValidator validator;
    private final StoreNormalizer normalizer;
    private final SaveStorePortOut saveStorePortOut;
    private final FindUserPortOut findUserPortOut;
    private final SaveUserPortOut saveUserPortOut;

    @Override
    public StoreResponse create(CreateStoreRequest request) {
        log.info("Iniciando criação de loja | e-mail: {}", request.commercialEmail());

        validator.validateCreation(request.commercialEmail(), request.cnpj(), request.slug(), request.ownerUuid());

        User owner = findUserPortOut.findByUuidOrThrow(request.ownerUuid());
        owner.grantStoreOwnership();

        Store store = mapper.createStoreRequestToEntity(request);
        store.setActive(true);
        store.setOwner(owner);

        normalizer.normalizeData(store);

        saveUserPortOut.save(owner);
        Store createdStore = saveStorePortOut.save(store);

        log.info("Loja criada com sucesso | uuid: {} | e-mail: {}", createdStore.getUuid(), createdStore.getCommercialEmail());

        return mapper.entityToStoreResponse(createdStore);
    }
}

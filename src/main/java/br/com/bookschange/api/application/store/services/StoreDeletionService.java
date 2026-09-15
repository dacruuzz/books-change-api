package br.com.bookschange.api.application.store.services;

import br.com.bookschange.api.application.address.ports.out.DeleteAddressPortOut;
import br.com.bookschange.api.application.store.ports.out.DeleteStorePortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.domain.models.Store;
import br.com.bookschange.api.domain.models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreDeletionService {

    private final DeleteStorePortOut deleteStorePortOut;
    private final SaveUserPortOut saveUserPortOut;
    private final DeleteAddressPortOut deleteAddressPortOut;

    @Transactional
    public void delete(Store store) {
        log.info("Iniciando exclusão da loja | uuid: {}", store.getUuid());

        if (store.getAddress() != null) {
            deleteStoreAddress(store.getAddress());
        } else {
            log.warn("Nenhum endereço encontrado associado à loja");
        }

        updateOwnerUserType(store.getOwner());

        deleteStorePortOut.delete(store);
        log.info("Loja excluída com sucesso | storeUuid: {}", store.getUuid());
    }

    private void updateOwnerUserType(User owner) {
        log.debug("Buscando usuário para alteração do tipo do usuário | ownerUuid: {}", owner.getUuid());

        owner.revokeStoreOwnership();

        saveUserPortOut.save(owner);
        log.debug("Tipo do usuário atualizado para DEFAULT | ownerUuid: {}", owner.getUuid());
    }

    private void deleteStoreAddress(Address address) {
        log.info("Excluindo endereço da loja | addressUuid: {}", address.getUuid());

        deleteAddressPortOut.delete(address);
    }
}

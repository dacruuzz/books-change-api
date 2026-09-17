package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.store.ports.out.FindStorePortOut;
import br.com.bookschange.api.application.store.ports.out.SaveStorePortOut;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.in.InactiveActiveUserPortIn;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InactiveActiveUserUseCase implements InactiveActiveUserPortIn {

    private static final String ACTIVE = "active";
    private static final String INACTIVE = "inactive";

    private final UserMapper mapper;
    private final FindUserPortOut findUserPortOut;
    private final SaveUserPortOut saveUserPortOut;
    private final FindBookPortOut findBookPortOut;
    private final SaveBookPortOut saveBookPortOut;
    private final FindStorePortOut findStorePortOut;
    private final SaveStorePortOut saveStorePortOut;
    private final SaveAddressPortOut saveAddressPortOut;

    @Override
    @Transactional
    public UserResponseDTO inactiveActive(UUID uuid, String pathParam) {
        String action = pathParam.equals(INACTIVE) ? "Inativando usuário" : "Ativando usuário";
        log.info("{} | ownerUuid: {}", action, uuid);

        User foundUser = findUserPortOut.findByUuidOrThrow(uuid);

        inactiveOrActiveUser(pathParam, foundUser);
        inactiveOrActiveBooks(pathParam, foundUser);
        inactiveOrActiveStore(pathParam, foundUser);

        User user = saveUserPortOut.save(foundUser);

        action = pathParam.equals(INACTIVE) ? "Usuário inativado com sucesso" : "Usuário ativado com sucesso";

        log.info("{} | ownerUuid: {} | status: {}", action, foundUser.getUuid(), pathParam);
        return mapper.entityToUserResponseDto(user);
    }

    private void inactiveOrActiveBooks(String pathParam, User owner) {
        String action = pathParam.equals(INACTIVE) ? "Inativando livro(s)" : "Ativando livro(s)";
        List<Book> books = findBookPortOut.findAllByOwnerUuid(owner.getUuid());

        if (!books.isEmpty()) {
            log.debug("{} | livro(s) encontrados: {}", action, books.size());
            books.forEach(book -> book.setActive(owner.isActive()));
            saveBookPortOut.saveAll(books);
        }
    }

    private void inactiveOrActiveStore(String pathParam, User owner) {
        String actionStore = pathParam.equals(INACTIVE) ? "Inativando loja" : "Ativando loja";
        String actionAddress = pathParam.equals(INACTIVE) ? "Inativando endereço" : "Ativando endereço";
        findStorePortOut.findByOwnerUuid(owner.getUuid())
                .ifPresent(store -> {
                    if (store.getAddress() != null) {
                        log.debug("{} | addressUuid: {}", actionAddress, store.getAddress().getUuid());
                        Address address = store.getAddress();
                        address.setActive(owner.isActive());
                        saveAddressPortOut.save(address);
                    }

                    log.debug("{} | storeUuid: {}", actionStore, store.getUuid());
                    store.setActive(owner.isActive());
                    saveStorePortOut.save(store);
        });
    }

    private void inactiveOrActiveUser(String pathParam, User foundUser) {
        if (pathParam.equalsIgnoreCase(ACTIVE)) {
            if (foundUser.isActive()) {
                log.warn("O usuário já está ativo | ownerUuid: {} | status: {}", foundUser.getUuid(), pathParam);
                throw new BusinessException("O usuário já está ativo");
            }
            foundUser.setActive(true);
        } else if (pathParam.equalsIgnoreCase(INACTIVE)) {
            if (!foundUser.isActive()) {
                log.warn("O usuário já está inativo | ownerUuid: {} | status: {}", foundUser.getUuid(), pathParam);
                throw new BusinessException("O usuário já está inativo");
            }
            foundUser.setActive(false);
        } else {
            log.error("Ação inválida. Use 'active' ou 'inactive' | ownerUuid: {} | pathParam: {}", foundUser.getUuid(), pathParam);
            throw new BusinessException("Ação inválida. Use 'active' ou 'inactive'");
        }
    }
}

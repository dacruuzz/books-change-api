package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.in.FindUserPortIn;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.domain.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindUserUseCase implements FindUserPortIn {

    private final UserMapper mapper;
    private final FindUserPortOut findUserPortOut;

    @Override
    public UserResponseDTO findByUuid(UUID uuid) {
        log.info("Buscando usuário | uuid: {}", uuid);

        User user = findUserPortOut.findByUuidOrThrow(uuid);

        log.info("Usuário encontrado | uuid: {} | e-mail: {}", user.getUuid(), user.getEmail());
        return mapper.entityToUserResponseDto(user);
    }
}

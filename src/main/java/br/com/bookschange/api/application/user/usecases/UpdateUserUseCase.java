package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.UpdateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.in.UpdateUserPortIn;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.models.User;
import br.com.bookschange.api.shared.services.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserUseCase implements UpdateUserPortIn {

    private final UserMapper mapper;
    private final TextNormalizer normalizer;
    private final FindUserPortOut findUserPortOut;
    private final SaveUserPortOut saveUserPortOut;

    @Override
    public UserResponseDTO update(UUID uuid, UpdateUserRequestDTO request) {
        log.info("Buscando usuário para edição | uuid: {}", uuid);

        User user = findUserPortOut.findByUuidOrThrow(uuid);

        mapper.updateUserRequestDtoToEntity(request, user);

        user.setName(normalizer.normalizeToUpperCase(user.getName()));

        User updatedUser = saveUserPortOut.save(user);

        log.info("Edição de usuário feita com sucesso");
        return mapper.entityToUserResponseDto(updatedUser);
    }
}

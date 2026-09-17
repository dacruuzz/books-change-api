package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.CreateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.in.CreateUserPortIn;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.User;
import br.com.bookschange.api.shared.services.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUserPortIn {

    private final UserMapper mapper;
    private final TextNormalizer normalizer;
    private final SaveUserPortOut saveUserPortOut;
    private final FindUserPortOut findUserPortOut;

    @Override
    public UserResponseDTO create(String userType, CreateUserRequestDTO request) {
        log.info("Iniciando criação de usuário | email: {}", request.email());

        validateData(request);

        User user = mapper.createUserRequestDtoToEntity(request);
        UserType parsedUserType = UserType.fromValue(userType);

        normalizeData(user);

        user.setUserType(parsedUserType);

        User createdUser = saveUserPortOut.save(user);

        log.info("Usuário criado com sucesso | uuid: {} | tipo: {}", createdUser.getUuid(), createdUser.getUserType());

        return mapper.entityToUserResponseDto(createdUser);
    }

    private void normalizeData(User user) {
        user.setName(normalizer.normalizeToUpperCase(user.getName()));
        user.setCpf(normalizer.normalizeCpf(user.getCpf()));
        user.setEmail(normalizer.normalizeEmail(user.getEmail()));
    }

    private void validateData(CreateUserRequestDTO request) {
        validateCpf(request);
        validateEmail(request);
    }

    private void validateEmail(CreateUserRequestDTO request) {
        String normalizedEmail = normalizer.normalizeEmail(request.email());
        boolean emailAlreadyExists = findUserPortOut.existsByEmail(normalizedEmail);

        if (emailAlreadyExists) {
            log.warn("Tentativa de cadastro com e-mail já existente | email: {}", normalizedEmail);
            throw new BusinessException("Já existe um usuário cadastrado com esse e-mail");
        }
    }

    private void validateCpf(CreateUserRequestDTO request) {
        String normalizedCpf = normalizer.normalizeCpf(request.cpf());
        boolean cpfAlreadyExists = findUserPortOut.existsByCpf(normalizedCpf);

        if (cpfAlreadyExists) {
            log.warn("Tentativa de cadastro com CPF já existente");
            throw new BusinessException("Já existe um usuário cadastrado com esse cpf");
        }
        
        if (normalizedCpf.length() != 11) {
            log.warn("Tentativa de cadastro de cpf com tamanho inválido");
            throw new BusinessException("O tamanho do cpf está incorreto");
        }
    }
}

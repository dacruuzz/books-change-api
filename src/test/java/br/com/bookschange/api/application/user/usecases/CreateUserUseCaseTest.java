package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.CreateUserRequest;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponse;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.Gender;
import br.com.bookschange.api.domain.enums.UserType;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.User;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    public static final String NORMALIZED_CPF = "00000000000";
    public static final String NORMALIZED_EMAIL = "USER@EMAIL.COM";

    @Mock private UserMapper mapper;
    @Mock private TextNormalizer normalizer;
    @Mock private SaveUserPortOut saveUserPortOut;
    @Mock private FindUserPortOut findUserPortOut;

    private CreateUserRequest request;
    private User user;

    @InjectMocks
    private CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();

        request = new CreateUserRequest(
                "User",
                "000.000.000-00",
                Gender.NOT_INFORMED,
                "user@email.com",
                "password",
                LocalDate.now()
        );

        user = new User();
        user.setUuid(uuid);
        user.setName(request.name());
        user.setCpf(request.cpf());
        user.setGender(request.gender());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setBirthDate(request.birthDate());
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);

        when(normalizer.normalizeCpf(request.cpf())).thenReturn(NORMALIZED_CPF);
        when(findUserPortOut.existsByCpf(NORMALIZED_CPF)).thenReturn(false);
        when(normalizer.normalizeEmail(request.email())).thenReturn(NORMALIZED_EMAIL);
        when(findUserPortOut.existsByEmail(NORMALIZED_EMAIL)).thenReturn(false);
        when(mapper.createUserRequestToEntity(request)).thenReturn(user);
        when(normalizer.normalizeToUpperCase(user.getName())).thenReturn("USER");
        when(normalizer.normalizeCpf(user.getCpf())).thenReturn(NORMALIZED_CPF);
        when(normalizer.normalizeEmail(user.getEmail())).thenReturn(NORMALIZED_EMAIL);
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.create("DEFAULT", request);

        assertEquals(expectedResponse, response);
        assertEquals(UserType.DEFAULT, user.getUserType());
        verify(normalizer, times(2)).normalizeEmail(anyString());
        verify(normalizer, times(2)).normalizeCpf(anyString());
        verify(normalizer).normalizeToUpperCase(anyString());
        verify(mapper).createUserRequestToEntity(request);
        verify(mapper).entityToUserResponse(user);
        verify(findUserPortOut).existsByCpf(NORMALIZED_CPF);
        verify(findUserPortOut).existsByEmail(NORMALIZED_EMAIL);
        verify(saveUserPortOut).save(user);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando já existe um usuário com cpf informado")
    void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
        when(normalizer.normalizeCpf(request.cpf())).thenReturn(NORMALIZED_CPF);
        when(findUserPortOut.existsByCpf(NORMALIZED_CPF)).thenReturn(true);

        BusinessException e = assertThrows(BusinessException.class, () -> useCase.create("DEFAULT", request));

        assertEquals("Já existe um usuário cadastrado com esse cpf", e.getMessage());
        verify(saveUserPortOut, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o cpf for menor que 11 dígitos")
    void shouldThrowBusinessExceptionWhenCpfLengthIsLessThan11Digits() {
        CreateUserRequest requestCpfInvalid = new CreateUserRequest(
                request.name(),
                "000.000.000-0",
                request.gender(),
                request.email(),
                request.password(),
                request.birthDate()
        );

        when(normalizer.normalizeCpf(requestCpfInvalid.cpf())).thenReturn("0000000000");
        when(findUserPortOut.existsByCpf("0000000000")).thenReturn(false);

        BusinessException e = assertThrows(BusinessException.class,
                () -> useCase.create("DEFAULT", requestCpfInvalid));

        assertEquals("O tamanho do cpf está incorreto", e.getMessage());
        verify(saveUserPortOut, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o cpf for maior que 11 dígitos")
    void shouldThrowBusinessExceptionWhenCpfLengthIsMoreThan11Digits() {
        CreateUserRequest requestCpfInvalid = new CreateUserRequest(
                request.name(),
                "000.000.000-000",
                request.gender(),
                request.email(),
                request.password(),
                request.birthDate()
        );

        when(normalizer.normalizeCpf(requestCpfInvalid.cpf())).thenReturn("000000000000");
        when(findUserPortOut.existsByCpf("000000000000")).thenReturn(false);

        BusinessException e = assertThrows(BusinessException.class,
                () -> useCase.create("DEFAULT", requestCpfInvalid));

        assertEquals("O tamanho do cpf está incorreto", e.getMessage());
        verify(saveUserPortOut, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando já existe um usuário com o email informado")
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        when(normalizer.normalizeCpf(request.cpf())).thenReturn(NORMALIZED_CPF);
        when(findUserPortOut.existsByCpf(NORMALIZED_CPF)).thenReturn(false);
        when(normalizer.normalizeEmail(request.email())).thenReturn(NORMALIZED_EMAIL);
        when(findUserPortOut.existsByEmail(NORMALIZED_EMAIL)).thenReturn(true);

        BusinessException e = assertThrows(BusinessException.class, () -> useCase.create("DEFAULT", request));

        assertEquals("Já existe um usuário cadastrado com esse e-mail", e.getMessage());
        verify(saveUserPortOut, never()).save(any());
    }
}
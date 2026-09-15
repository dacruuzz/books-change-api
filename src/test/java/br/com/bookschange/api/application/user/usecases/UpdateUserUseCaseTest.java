package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.UpdateUserRequest;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponse;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.application.user.ports.out.SaveUserPortOut;
import br.com.bookschange.api.domain.enums.Gender;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock private UserMapper mapper;
    @Mock private TextNormalizer normalizer;
    @Mock private FindUserPortOut findUserPortOut;
    @Mock private SaveUserPortOut saveUserPortOut;

    private UpdateUserRequest request;
    private UUID uuid;
    private User user;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        request = new UpdateUserRequest(
                "User",
                Gender.NOT_INFORMED,
                LocalDate.now()
        );

        user = new User();
        user.setUuid(uuid);
        user.setName(request.name());
        user.setGender(request.gender());
        user.setBirthDate(request.birthDate());
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        UserResponse expectedResponse = mock(UserResponse.class);

        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        doNothing().when(mapper).updateUserRequestToEntity(request, user);
        when(normalizer.normalizeToUpperCase(user.getName())).thenReturn("USER");
        when(saveUserPortOut.save(user)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.update(uuid, request);

        assertEquals(expectedResponse, response);
        assertEquals("USER", user.getName());
        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(mapper).updateUserRequestToEntity(request, user);
        verify(normalizer).normalizeToUpperCase(anyString());
        verify(saveUserPortOut).save(user);
        verify(mapper).entityToUserResponse(user);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o usuário não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenUserWasNotFindByUuid() {
        when(findUserPortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Usuário não encontrado"));
        assertThrows(NotFoundException.class, () -> useCase.update(uuid, request));
        verify(saveUserPortOut, never()).save(any());
    }
}
package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponse;
import br.com.bookschange.api.application.user.mappers.UserMapper;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserUseCaseTest {

    @Mock private UserMapper mapper;
    @Mock private FindUserPortOut findUserPortOut;

    @InjectMocks
    private FindUserUseCase useCase;

    @Test
    @DisplayName("Deve buscar um usuário com sucesso")
    void shouldFindUserSuccessfully() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        UserResponse expectedResponse = mock(UserResponse.class);

        when(findUserPortOut.findByUuidOrThrow(uuid)).thenReturn(user);
        when(mapper.entityToUserResponse(user)).thenReturn(expectedResponse);

        UserResponse response = useCase.findByUuid(uuid);

        assertEquals(expectedResponse, response);
        verify(findUserPortOut).findByUuidOrThrow(uuid);
        verify(mapper).entityToUserResponse(user);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o usuário não é encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenUserWasNotFindByUuid() {
        UUID uuid = UUID.randomUUID();

        when(findUserPortOut.findByUuidOrThrow(uuid)).thenThrow(new NotFoundException("Usuário não encontrado"));
        assertThrows(NotFoundException.class, () -> useCase.findByUuid(uuid));
        verify(mapper, never()).entityToUserResponse(any());
    }
}
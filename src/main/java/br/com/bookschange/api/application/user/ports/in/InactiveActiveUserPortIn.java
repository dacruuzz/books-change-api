package br.com.bookschange.api.application.user.ports.in;

import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;

import java.util.UUID;

public interface InactiveActiveUserPortIn {
    UserResponseDTO inactiveActive(UUID uuid, String pathParam);
}

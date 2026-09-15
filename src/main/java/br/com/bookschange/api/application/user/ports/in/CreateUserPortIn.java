package br.com.bookschange.api.application.user.ports.in;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.CreateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;

public interface CreateUserPortIn {
    UserResponseDTO create(String userType, CreateUserRequestDTO request);
}

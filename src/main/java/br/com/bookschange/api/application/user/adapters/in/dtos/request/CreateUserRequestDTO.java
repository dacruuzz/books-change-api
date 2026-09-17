package br.com.bookschange.api.application.user.adapters.in.dtos.request;

import br.com.bookschange.api.domain.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateUserRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O cpf é obrigatório")
        String cpf,

        @NotNull(message = "O gênero é obrigatório")
        Gender gender,

        @Email(message = "O formato do e-mail não está válido")
        @NotBlank(message = "O email é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String password,

        @NotNull(message = "A data de nascimento é obrigatória")
        LocalDate birthDate
) {
}

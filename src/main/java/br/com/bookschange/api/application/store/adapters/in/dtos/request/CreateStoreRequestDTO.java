package br.com.bookschange.api.application.store.adapters.in.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.UUID;

public record CreateStoreRequestDTO(
        @NotBlank(message = "O nome da loja é obrigatório")
        String name,

        @CNPJ(message = "CNPJ inválido")
        @NotBlank(message = "O CNPJ da loja é obrigatório")
        String cnpj,

        @Email(message = "O formato do e-mail está inválido")
        @NotBlank(message = "O e-mail da loja é obrigatório")
        String commercialEmail,

        String phone,

        @NotBlank(message = "O identificador da loja é obrigatório")
        String slug,

        String description,

        @NotNull(message = "O uuid do usuário é obrigatório")
        UUID ownerUuid
) {
}

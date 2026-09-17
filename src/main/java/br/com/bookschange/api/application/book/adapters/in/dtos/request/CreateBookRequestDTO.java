package br.com.bookschange.api.application.book.adapters.in.dtos.request;

import br.com.bookschange.api.domain.enums.CurrentCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateBookRequestDTO(
        @NotBlank(message = "O nome do livro é obrigatório")
        String name,

        @NotBlank(message = "O nome do autor é obrigatório")
        String author,

        @NotBlank(message = "O nome da editora é obrigatório")
        String publisher,

        String resume,

        @NotEmpty(message = "Ao menos uma categoria do livro é obrigatória")
        List<@NotNull UUID> categories,

        @NotNull(message = "O estado atual do livro é obrigatório")
        CurrentCondition currentCondition,

        @NotNull(message = "O uuid do usuário é obrigatório")
        UUID ownerUuid
) {
}

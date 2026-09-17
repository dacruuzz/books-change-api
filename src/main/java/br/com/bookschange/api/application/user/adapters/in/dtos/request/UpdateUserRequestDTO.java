package br.com.bookschange.api.application.user.adapters.in.dtos.request;

import br.com.bookschange.api.domain.enums.Gender;

import java.time.LocalDate;

public record UpdateUserRequestDTO(
        String name,
        Gender gender,
        LocalDate birthDate
) {
}

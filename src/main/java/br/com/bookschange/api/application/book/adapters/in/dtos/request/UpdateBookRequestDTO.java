package br.com.bookschange.api.application.book.adapters.in.dtos.request;

import br.com.bookschange.api.domain.enums.CurrentCondition;

import java.util.List;
import java.util.UUID;

public record UpdateBookRequestDTO(
        String name,
        String author,
        String publisher,
        String resume,
        List<UUID> categories,
        CurrentCondition currentCondition
) {
}

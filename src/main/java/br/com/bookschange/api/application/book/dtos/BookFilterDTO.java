package br.com.bookschange.api.application.book.dtos;

import br.com.bookschange.api.domain.enums.CurrentCondition;

import java.util.List;
import java.util.UUID;

public record BookFilterDTO(
        String name,
        String author,
        String publisher,
        List<UUID> bookCategoriesUuids,
        CurrentCondition currentCondition
) {
}

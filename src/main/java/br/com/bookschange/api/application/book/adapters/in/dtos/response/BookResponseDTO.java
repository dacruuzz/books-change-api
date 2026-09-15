package br.com.bookschange.api.application.book.adapters.in.dtos.response;

import br.com.bookschange.api.domain.enums.CurrentCondition;
import br.com.bookschange.api.shared.dtos.SelectOptionDTO;

import java.util.List;
import java.util.UUID;

public record BookResponseDTO(
        UUID uuid,
        String name,
        String author,
        String publisher,
        String resume,
        List<SelectOptionDTO> categories,
        CurrentCondition currentCondition,
        UUID ownerUuid
) {
}

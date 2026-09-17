package br.com.bookschange.api.application.book.ports.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;

import java.util.UUID;

public interface FindBookPortIn {
    BookResponseDTO findByUuid(UUID uuid);
}

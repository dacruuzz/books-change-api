package br.com.bookschange.api.application.book.ports.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.UpdateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;

import java.util.UUID;

public interface UpdateBookPortIn {
    BookResponseDTO update(UUID uuid, UpdateBookRequestDTO request);
}

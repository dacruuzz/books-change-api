package br.com.bookschange.api.application.book.ports.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.CreateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;

public interface CreateBookPortIn {
    BookResponseDTO create(CreateBookRequestDTO request);
}

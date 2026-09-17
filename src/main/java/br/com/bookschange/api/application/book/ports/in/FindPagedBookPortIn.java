package br.com.bookschange.api.application.book.ports.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;

public interface FindPagedBookPortIn {
    PageDTO<BookResponseDTO> findAllPaged(int page, int pageSize);
}

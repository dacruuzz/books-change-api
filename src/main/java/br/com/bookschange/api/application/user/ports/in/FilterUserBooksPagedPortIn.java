package br.com.bookschange.api.application.user.ports.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;

import java.util.UUID;

public interface FilterUserBooksPagedPortIn {

    PageDTO<BookResponseDTO> filter(UUID ownerUuid, FilterBookRequestDTO request, int page, int pageSize);
}

package br.com.bookschange.api.application.user.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.user.ports.in.FilterUserBooksPagedPortIn;
import br.com.bookschange.api.application.user.ports.out.FilterUserBooksPagedPortOut;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;
import br.com.bookschange.infrastructure.shared.pagination.PageMapper;
import br.com.bookschange.infrastructure.shared.pagination.PaginationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterUserBooksPagedUseCase implements FilterUserBooksPagedPortIn {

    private final BookMapper bookMapper;
    private final PageMapper pageMapper;
    private final FilterUserBooksPagedPortOut filterUserBooksPagedPortOut;

    @Override
    public PageDTO<BookResponseDTO> filter(UUID ownerUuid, FilterBookRequestDTO request, int page, int pageSize) {
        log.info("Iniciando filtro paginado dos livros do usuário | ownerUuid: {}", ownerUuid);

        BookFilterDTO filter = bookMapper.filterBookRequestToBookFilter(request);
        Pageable pageable = PaginationFactory.createPageable(page, pageSize);
        Page<Book> userBooks = filterUserBooksPagedPortOut.find(ownerUuid, filter, pageable);
        Page<BookResponseDTO> mappedPage = userBooks.map(bookMapper::entityToBookResponse);

        log.info("Busca de livro realizada. Encontrando {} livros", mappedPage.getTotalElements());
        return pageMapper.toPageDTO(mappedPage);
    }
}

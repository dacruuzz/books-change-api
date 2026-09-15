package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.in.FindPagedBookPortIn;
import br.com.bookschange.api.application.book.ports.out.FindPagedBooksPortOut;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;
import br.com.bookschange.infrastructure.shared.pagination.PageMapper;
import br.com.bookschange.infrastructure.shared.pagination.PaginationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindPagedBooksUseCase implements FindPagedBookPortIn {

    private final BookMapper bookMapper;
    private final PageMapper pageMapper;
    private final FindPagedBooksPortOut findPagedBooksPortOut;


    @Override
    public PageDTO<BookResponseDTO> findAllPaged(int page, int pageSize) {
        log.info("Buscando livros paginados");

        Pageable pageable = PaginationFactory.createPageable(page, pageSize);

        Page<Book> books = findPagedBooksPortOut.findAllActivePaged(pageable);

        Page<BookResponseDTO> mappedPage = books.map(bookMapper::entityToBookResponseDto);

        log.info("Busca de livro realizada. Encontrando {} livros", mappedPage.getTotalElements());
        return pageMapper.toPageDTO(mappedPage);
    }
}

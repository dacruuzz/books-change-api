package br.com.bookschange.api.application.user.ports.out;

import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
import br.com.bookschange.api.domain.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FilterUserBooksPagedPortOut {
    Page<Book> find(UUID ownerUuid, BookFilterDTO filter, Pageable pageable);
}

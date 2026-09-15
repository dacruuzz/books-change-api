package br.com.bookschange.api.application.user.adapters.out;

import br.com.bookschange.api.application.book.adapters.out.repositories.BookJpaRepository;
import br.com.bookschange.api.application.book.adapters.out.repositories.specification.BookSpec;
import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
import br.com.bookschange.api.application.user.ports.out.FilterUserBooksPagedPortOut;
import br.com.bookschange.api.domain.models.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilterUserBooksPagedAdapter implements FilterUserBooksPagedPortOut {

    private final BookJpaRepository repository;

    @Override
    public Page<Book> find(UUID ownerUuid, BookFilterDTO filter, Pageable pageable) {
        Specification<Book> spec = BookSpec.filter(ownerUuid, filter);

        return repository.findAll(spec, pageable);
    }
}

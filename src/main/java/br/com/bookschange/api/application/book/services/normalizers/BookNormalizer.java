package br.com.bookschange.api.application.book.services.normalizers;

import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.shared.services.validators.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookNormalizer {

    private final TextNormalizer normalizer;

    public void normalizeData(Book book) {
        book.setName(normalizer.normalizeToUpperCase(book.getName()));
        book.setAuthor(normalizer.normalizeToUpperCase(book.getAuthor()));
        book.setPublisher(normalizer.normalizeToUpperCase(book.getPublisher()));
        book.setResume(normalizer.normalizeToUpperCase(book.getResume()));
    }
}

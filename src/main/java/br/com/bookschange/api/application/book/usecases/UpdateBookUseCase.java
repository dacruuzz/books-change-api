package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.UpdateBookRequest;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponse;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.in.UpdateBookPortIn;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.book.services.normalizers.BookNormalizer;
import br.com.bookschange.api.application.book.services.validators.BookValidator;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateBookUseCase implements UpdateBookPortIn {

    private final BookMapper mapper;
    private final BookNormalizer normalizer;
    private final BookValidator validator;
    private final FindBookPortOut findBookPortOut;
    private final FindCategoryPortOut findCategoryPortOut;
    private final SaveBookPortOut saveBookPortOut;

    @Override
    public BookResponse update(UUID uuid, UpdateBookRequest request) {
        log.info("Atualizando livro | uuid: {}", uuid);

        Book book = findBookPortOut.findByUuidOrThrow(uuid);
        List<Category> categories = findCategoryPortOut.findAllByUuids(request.categories());

        validator.validateCategories(categories);

        mapper.updateBookFromRequest(request, book);

        book.replaceCategories(categories);

        normalizer.normalizeData(book);

        Book updatedBook = saveBookPortOut.save(book);

        log.info("Livro atualizado com sucesso | uuid: {}", updatedBook.getUuid());

        return mapper.entityToBookResponse(updatedBook);
    }
}

package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.CreateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.in.CreateBookPortIn;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.book.services.normalizers.BookNormalizer;
import br.com.bookschange.api.application.book.services.validators.BookValidator;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.Category;
import br.com.bookschange.api.domain.models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateBookUseCase implements CreateBookPortIn {

    private final BookMapper mapper;
    private final BookNormalizer normalizer;
    private final BookValidator validator;
    private final SaveBookPortOut saveBookPortOut;
    private final FindUserPortOut findUserPortOut;
    private final FindCategoryPortOut findCategoryPortOut;

    @Override
    @Transactional
    public BookResponseDTO create(CreateBookRequestDTO request) {
        log.info("Criando livro | titulo: {}", request.name());

        User owner = findUserPortOut.findByUuidOrThrow(request.ownerUuid());
        List<Category> categories = findCategoryPortOut.findAllByUuids(request.categories());

        validator.validateCategories(categories);

        Book book = mapper.createBookRequestToEntity(request);
        book.setOwner(owner);
        book.addCategories(categories);

        normalizer.normalizeData(book);

        Book createdBook = saveBookPortOut.save(book);

        log.info("Livro criado com sucesso | uuid: {} | título: {}", createdBook.getUuid(), createdBook.getName());
        return mapper.entityToBookResponseDto(createdBook);
    }
}

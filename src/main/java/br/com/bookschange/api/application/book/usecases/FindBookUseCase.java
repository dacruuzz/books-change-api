package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.in.FindBookPortIn;
import br.com.bookschange.api.application.book.ports.out.FindBookPortOut;
import br.com.bookschange.api.domain.models.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindBookUseCase implements FindBookPortIn {

    private final BookMapper mapper;
    private final FindBookPortOut findBookPortOut;

    @Override
    public BookResponseDTO findByUuid(UUID uuid) {
        log.info("Buscando livro | uuid: {}", uuid);

        Book foundBook = findBookPortOut.findByUuidOrThrow(uuid);

        log.info("Livro encontrado | uuid: {} | título: {}", foundBook.getUuid(), foundBook.getName());
        return mapper.entityToBookResponseDto(foundBook);
    }
}

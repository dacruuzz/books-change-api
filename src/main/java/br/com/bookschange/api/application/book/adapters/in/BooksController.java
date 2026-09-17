package br.com.bookschange.api.application.book.adapters.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.CreateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.request.UpdateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.ports.in.*;
import br.com.bookschange.infrastructure.shared.ApiResponseBuilder;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

    private final ApiResponseBuilder apiResponseBuilder;
    private final CreateBookPortIn createBookPortIn;
    private final UpdateBookPortIn updateBookPortIn;
    private final FindBookPortIn findBookPortIn;
    private final FindPagedBookPortIn findPagedBookPortIn;
    private final DeleteBookPortIn deleteBookPortIn;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateBookRequestDTO request) {
        BookResponseDTO response = createBookPortIn.create(request);
        return apiResponseBuilder.buildCreated(response);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable("uuid") UUID uuid,
                                    @RequestBody @Valid UpdateBookRequestDTO request
    ) {
        BookResponseDTO response = updateBookPortIn.update(uuid, request);
        return apiResponseBuilder.buildSuccess(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> findByUuid(@PathVariable("uuid") UUID uuid) {
        BookResponseDTO response = findBookPortIn.findByUuid(uuid);
        return apiResponseBuilder.buildSuccess(response);
    }

    @GetMapping("/paged")
    private ResponseEntity<?> findAllPaged(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageDTO<BookResponseDTO> response = findPagedBookPortIn.findAllPaged(page, pageSize);
        return apiResponseBuilder.buildSuccessPaged(response);
    }

    @DeleteMapping("/{uuid}")
    private ResponseEntity<?> delete(@PathVariable("uuid") UUID uuid) {
        deleteBookPortIn.delete(uuid);
        return apiResponseBuilder.buildDeleted();
    }
}

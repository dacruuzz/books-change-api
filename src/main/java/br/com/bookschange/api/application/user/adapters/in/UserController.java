package br.com.bookschange.api.application.user.adapters.in;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.request.CreateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.request.UpdateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.application.user.ports.in.*;
import br.com.bookschange.infrastructure.shared.ApiResponseBuilder;
import br.com.bookschange.infrastructure.shared.pagination.PageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ApiResponseBuilder apiResponseBuilder;
    private final CreateUserPortIn createUserPortIn;
    private final FindUserPortIn findUserPortIn;
    private final FilterUserBooksPagedPortIn filterUserBooksPagedPortIn;
    private final InactiveActiveUserPortIn inactiveActiveUserPortIn;
    private final UpdateUserPortIn updateUserPortIn;
    private final DeleteUserPortIn deleteUserPortIn;

    @PostMapping("/{userType}")
    public ResponseEntity<?> create(@PathVariable String userType, @RequestBody @Valid CreateUserRequestDTO request) {
        UserResponseDTO response = createUserPortIn.create(userType, request);
        return apiResponseBuilder.buildCreated(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> findByUuid(@PathVariable String uuid) {
        UserResponseDTO response = findUserPortIn.findByUuid(UUID.fromString(uuid));
        return apiResponseBuilder.buildSuccess(response);
    }

    @PostMapping("/{uuid}/books/search")
    public ResponseEntity<?> findUserBooks(@PathVariable UUID uuid,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestBody FilterBookRequestDTO request
    ) {
        PageDTO<BookResponseDTO> response = filterUserBooksPagedPortIn.filter(uuid, request, page, pageSize);
        return apiResponseBuilder.buildSuccessPaged(response);
    }

    @PutMapping("/{uuid}/param/{param}")
    public ResponseEntity<?> inactiveActive(@PathVariable String uuid,
                                            @PathVariable String param
    ) {
        UserResponseDTO response = inactiveActiveUserPortIn.inactiveActive(UUID.fromString(uuid), param);
        return apiResponseBuilder.buildSuccess(response);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable String uuid,
                                    @RequestBody @Valid UpdateUserRequestDTO request
    ) {
        UserResponseDTO response = updateUserPortIn.update(UUID.fromString(uuid), request);
        return apiResponseBuilder.buildSuccess(response);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        deleteUserPortIn.delete(uuid);
        return apiResponseBuilder.buildDeleted();
    }
}

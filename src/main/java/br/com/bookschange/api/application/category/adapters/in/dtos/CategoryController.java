package br.com.bookschange.api.application.category.adapters.in.dtos;

import br.com.bookschange.api.application.category.adapters.in.dtos.request.CreateCategoryRequestDTO;
import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponseDTO;
import br.com.bookschange.api.application.category.ports.in.CreateCategoryPortIn;
import br.com.bookschange.api.application.category.ports.in.DeleteCategoryPortIn;
import br.com.bookschange.api.application.category.ports.in.FindCategoryPortIn;
import br.com.bookschange.infrastructure.shared.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ApiResponseBuilder apiResponseBuilder;
    private final CreateCategoryPortIn createCategoryPortIn;
    private final FindCategoryPortIn findCategoryPortIn;
    private final DeleteCategoryPortIn deleteCategoryPortIn;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCategoryRequestDTO request) {
        CategoryResponseDTO response = createCategoryPortIn.create(request);
        return apiResponseBuilder.buildCreated(response);
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<CategoryResponseDTO> responseList = findCategoryPortIn.findAll();
        return apiResponseBuilder.buildList(Collections.singletonList(responseList));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> findByUuid(@PathVariable UUID uuid) {
        CategoryResponseDTO response = findCategoryPortIn.findByUuid(uuid);
        return apiResponseBuilder.buildSuccess(response);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        deleteCategoryPortIn.delete(uuid);
        return apiResponseBuilder.buildDeleted();
    }
}

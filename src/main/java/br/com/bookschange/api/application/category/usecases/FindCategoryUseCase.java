package br.com.bookschange.api.application.category.usecases;

import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponseDTO;
import br.com.bookschange.api.application.category.mappers.CategoryMapper;
import br.com.bookschange.api.application.category.ports.in.FindCategoryPortIn;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.domain.models.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindCategoryUseCase implements FindCategoryPortIn {

    private final CategoryMapper mapper;
    private final FindCategoryPortOut findCategoryPortOut;


    @Override
    public List<CategoryResponseDTO> findAll() {
        log.info("Buscando todas as categorias");

        List<Category> categoryList = findCategoryPortOut.findAll();

        return categoryList
                .stream()
                .map(mapper::entityToCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDTO findByUuid(UUID uuid) {
        log.info("Buscando categoria por uuid | uuid: {}", uuid);

        Category category = findCategoryPortOut.findByUuidOrThrow(uuid);
        return mapper.entityToCategoryResponseDto(category);
    }
}

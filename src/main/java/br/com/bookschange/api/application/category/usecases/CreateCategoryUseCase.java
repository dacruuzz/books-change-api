package br.com.bookschange.api.application.category.usecases;

import br.com.bookschange.api.application.category.adapters.in.dtos.request.CreateCategoryRequest;
import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponse;
import br.com.bookschange.api.application.category.mappers.CategoryMapper;
import br.com.bookschange.api.application.category.ports.in.CreateCategoryPortIn;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.application.category.ports.out.SaveCategoryPortOut;
import br.com.bookschange.api.domain.exceptions.BusinessException;
import br.com.bookschange.api.domain.models.Category;
import br.com.bookschange.api.shared.services.TextNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase implements CreateCategoryPortIn {

    private final CategoryMapper mapper;
    private final TextNormalizer normalizer;
    private final SaveCategoryPortOut saveCategoryPortOut;
    private final FindCategoryPortOut findCategoryPortOut;

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        log.info("Criando nova categoria | label: {}", request.label());

        validateSlug(request.slug());

        Category category = mapper.createCategoryToEntity(request);

        normalizeData(category);

        Category createdCategory = saveCategoryPortOut.save(category);

        log.info("Categoria criada com sucesso | uuid: {} | label: {}",
                createdCategory.getUuid(),
                createdCategory.getLabel()
        );
        return mapper.entityToCategoryResponse(createdCategory);
    }

    private void normalizeData(Category category) {
        if (category.getLabel() != null) {
            category.setLabel(normalizer.normalizeToUpperCase(category.getLabel()));
        }

        if (category.getSlug() != null) {
            category.setSlug(normalizer.normalizeToLowerCase(category.getSlug()));
        }
    }

    public void validateSlug(String slug) {
        String normalizedSlug = normalizer.normalizeToLowerCase(slug);

        if (findCategoryPortOut.existsBySlug(normalizedSlug)) {
            log.warn("Tentativa de cadastro com identificador existente");
            throw new BusinessException("Já existe uma categoria cadastrada com esse identificador");
        }
    }
}

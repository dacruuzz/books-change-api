package br.com.bookschange.api.application.category.mappers;

import br.com.bookschange.api.application.category.adapters.in.dtos.request.CreateCategoryRequestDTO;
import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponseDTO;
import br.com.bookschange.api.domain.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category createCategoryRequestDtoToEntity(CreateCategoryRequestDTO request);

    CategoryResponseDTO entityToCategoryResponseDto(Category category);
}

package br.com.bookschange.api.application.category.ports.in;

import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface FindCategoryPortIn {
    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO findByUuid(UUID uuid);
}

package br.com.bookschange.api.application.category.ports.in;

import br.com.bookschange.api.application.category.adapters.in.dtos.request.CreateCategoryRequestDTO;
import br.com.bookschange.api.application.category.adapters.in.dtos.response.CategoryResponseDTO;

public interface CreateCategoryPortIn {
    CategoryResponseDTO create(CreateCategoryRequestDTO request);
}

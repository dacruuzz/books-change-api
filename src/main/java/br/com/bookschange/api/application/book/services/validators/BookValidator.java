package br.com.bookschange.api.application.book.services.validators;

import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookValidator {

    /**
     * Verifica se a lista de categorias está vazia
     * @param categories
     */
    public void validateCategories(List<Category> categories) {
        if (categories.isEmpty()) {
            throw new NotFoundException("Categoria não encontrada");
        }
    }
}

package br.com.bookschange.api.application.book.mappers;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.CreateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.request.FilterBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.request.UpdateBookRequestDTO;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponseDTO;
import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.BookCategory;
import br.com.bookschange.api.shared.dtos.SelectOptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book createBookRequestToEntity(CreateBookRequestDTO request);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "bookCategories", ignore = true)
    void updateBookRequestDtoToEntity(UpdateBookRequestDTO request, @MappingTarget Book book);

    BookFilterDTO filterBookRequestToBookFilterDto(FilterBookRequestDTO request);

    @Mapping(target = "ownerUuid", source = "owner.uuid")
    @Mapping(target = "categories", source = "bookCategories")
    BookResponseDTO entityToBookResponseDto(Book book);

    default List<SelectOptionDTO> mapBookCategories(List<BookCategory> bookCategories) {
        return bookCategories
            .stream()
            .map(bookCategory ->
                new SelectOptionDTO(
                    bookCategory.getCategory().getUuid(),
                    bookCategory.getCategory().getLabel()
                ))
            .toList();
    }
}

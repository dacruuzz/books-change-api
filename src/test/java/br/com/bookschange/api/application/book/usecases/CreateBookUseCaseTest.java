package br.com.bookschange.api.application.book.usecases;

import br.com.bookschange.api.application.book.adapters.in.dtos.request.CreateBookRequest;
import br.com.bookschange.api.application.book.adapters.in.dtos.response.BookResponse;
import br.com.bookschange.api.application.book.mappers.BookMapper;
import br.com.bookschange.api.application.book.ports.out.SaveBookPortOut;
import br.com.bookschange.api.application.book.services.BookNormalizer;
import br.com.bookschange.api.application.book.services.BookValidator;
import br.com.bookschange.api.application.category.ports.out.FindCategoryPortOut;
import br.com.bookschange.api.application.user.ports.out.FindUserPortOut;
import br.com.bookschange.api.domain.enums.CurrentCondition;
import br.com.bookschange.api.domain.exceptions.NotFoundException;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.BookCategory;
import br.com.bookschange.api.domain.models.Category;
import br.com.bookschange.api.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseTest {

    @Mock private BookMapper mapper;
    @Mock private BookNormalizer normalizer;
    @Mock private BookValidator validator;
    @Mock private SaveBookPortOut saveBookPortOut;
    @Mock private FindUserPortOut findUserPortOut;
    @Mock private FindCategoryPortOut findCategoryPortOut;

    @InjectMocks
    private CreateBookUseCase useCase;

    public static final String LIVRO_NOME = "Livro nome";
    public static final String LIVRO_AUTOR = "Livro autor";
    public static final String LIVRO_EDITORA = "Livro editora";
    public static final String LIVRO_RESUMO = "Livro resumo";

    private BookResponse expectedResponse;
    private CreateBookRequest request;
    private Book mappedBook;
    private User owner;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        UUID ownerUuid = UUID.randomUUID();
        owner = mock(User.class);
        owner.setUuid(ownerUuid);

        categoryList = new ArrayList<>();
        categoryList.add(mock(Category.class));

        List<UUID> categoryUuidList = new ArrayList<>();
        categoryUuidList.add(UUID.randomUUID());

        List<BookCategory> bookCategoryList = new ArrayList<>();
        bookCategoryList.add(mock(BookCategory.class));

        request = new CreateBookRequest(
                LIVRO_NOME,
                LIVRO_AUTOR,
                LIVRO_EDITORA,
                LIVRO_RESUMO,
                categoryUuidList,
                CurrentCondition.NEW,
                ownerUuid
        );

        mappedBook = new Book();
        mappedBook.setName(request.name());
        mappedBook.setAuthor(request.author());
        mappedBook.setPublisher(request.publisher());
        mappedBook.setResume(request.resume());
        mappedBook.setBookCategories(bookCategoryList);
        mappedBook.setCurrentCondition(request.currentCondition());
        mappedBook.setOwner(owner);

        expectedResponse = mock(BookResponse.class);
    }

    @Test
    @DisplayName("Deve criar livro com sucesso")
    void shouldCreateBookSuccessfully() {
        when(findUserPortOut.findByUuidOrThrow(request.ownerUuid())).thenReturn(owner);
        when(findCategoryPortOut.findAllByUuids(request.categories())).thenReturn(categoryList);
        doNothing().when(validator).validateCategories(categoryList);
        when(mapper.createBookRequestToEntity(request)).thenReturn(mappedBook);
        doNothing().when(normalizer).normalizeData(mappedBook);
        when(saveBookPortOut.save(mappedBook)).thenReturn(mappedBook);
        when(mapper.entityToBookResponse(mappedBook)).thenReturn(expectedResponse);

        BookResponse result = useCase.create(request);

        assertEquals(expectedResponse, result);
        verify(validator, times(1)).validateCategories(anyList());
        verify(normalizer, times(1)).normalizeData(any());
        verify(mapper, times(1)).createBookRequestToEntity(any());
        verify(mapper, times(1)).entityToBookResponse(any());
        verify(findUserPortOut).findByUuidOrThrow(any());
        verify(findCategoryPortOut).findAllByUuids(anyList());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o usuário não for encontrado pelo uuid")
    void shouldThrowNotFoundExceptionWhenOwnerBookIsNotFoundByUuid() {
        when(findUserPortOut.findByUuidOrThrow(any())).thenThrow(new NotFoundException("Usuário não encontrado"));

        assertThrows(NotFoundException.class, () -> useCase.create(request));

        verify(validator, never()).validateCategories(anyList());
        verify(normalizer, never()).normalizeData(any());
        verify(mapper, never()).createBookRequestToEntity(any());
        verify(mapper, never()).entityToBookResponse(any());
        verify(findCategoryPortOut, never()).findAllByUuids(anyList());
    }
}
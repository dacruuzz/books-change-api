package br.com.bookschange.api.application.book.services;

import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.shared.services.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookNormalizerTest {

    @Mock private TextNormalizer normalizer;

    @InjectMocks
    private BookNormalizer service;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setName("Livro Teste");
        book.setAuthor("Autor Teste");
        book.setPublisher("Editora Teste");
        book.setResume("Resumo Teste");
    }

    @Test
    @DisplayName("Deve normalizar livro com sucesso")
    void shouldNormalizeAllBookFieldsSuccessfully() {
        when(normalizer.normalizeToUpperCase(book.getName())).thenReturn("LIVRO TESTE");
        when(normalizer.normalizeToUpperCase(book.getAuthor())).thenReturn("AUTOR TESTE");
        when(normalizer.normalizeToUpperCase(book.getPublisher())).thenReturn("EDITORA TESTE");
        when(normalizer.normalizeToUpperCase(book.getResume())).thenReturn("RESUMO TESTE");

        service.normalizeData(book);

        verify(normalizer, times(4)).normalizeToUpperCase(any());
    }
}
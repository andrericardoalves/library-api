package com.alves.libraryApi.service;

import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.repository.BookRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
public class BookServiceTest {

    @Autowired
    BookService bookService;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){

    }

    @Test
    public void shouldBeSaveBook(){
        Book book = createNewBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(Book.builder().id(1L).author("Andre")
                .title("Programming for All").isbn("0001").build());

        Book bookSaved = bookService.save(book);

        Assertions.assertNotNull(bookSaved.getId());
        Assertions.assertEquals("Andre", bookSaved.getAuthor());
        Assertions.assertEquals("Programming for All", bookSaved.getTitle());
        Assertions.assertEquals("0001", bookSaved.getIsbn());
    }

    private Book createNewBook() {
        return Book.builder().author("Andre").title("Programming for All").isbn("0001").build();
    }

    @Test
    public void shouldNotSaveABookWithDuplicatedIsbn(){
        Book book = createNewBook();
        String message = "Isbn already exist";
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> bookService.save(book));

        Assertions.assertEquals( message , exception.getMessage());
        MatcherAssert.assertThat(exception, CoreMatchers.instanceOf(BusinessException.class));
        Mockito.verify(repository, Mockito.never()).save(book);

    }

}

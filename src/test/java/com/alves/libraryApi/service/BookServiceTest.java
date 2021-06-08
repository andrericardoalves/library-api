package com.alves.libraryApi.service;

import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.repository.BookRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    private Book createNewBookWithId() {
        return Book.builder().id(1L).author("Andre").title("Programming for All").isbn("0001").build();
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

    @Test
    public void shouldFoundBookById(){
        Long id = 1L;
        Book book = createNewBookWithId();
        Mockito.when( repository.findById(id) ).thenReturn(Optional.of(book));

        Optional<Book> foundBook = repository.findById(id);

        MatcherAssert.assertThat(foundBook.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(foundBook.get().getId(), Matchers.equalTo(id));
        MatcherAssert.assertThat(foundBook.get().getAuthor(), Matchers.equalTo(book.getAuthor()));
        MatcherAssert.assertThat(foundBook.get().getIsbn(), Matchers.equalTo(book.getIsbn()));
        MatcherAssert.assertThat(foundBook.get().getTitle(), Matchers.equalTo(book.getTitle()));
    }

    @Test
    public void shouldNotFoundBookById(){
        Long id = 1L;
        Mockito.when( repository.findById(id) ).thenReturn(Optional.empty());

        Optional<Book> foundBook = repository.findById(id);

        MatcherAssert.assertThat(foundBook.isPresent(), Matchers.is(false));

    }

    @Test
    public void shouldBeDeleteBook(){
        Long id = 1L;

       Assertions.assertDoesNotThrow( () -> bookService.delete(id));  ;

        Mockito.verify(repository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void shouldFailDeleteBook(){
        Long id = null;

        Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.delete(id) );

        Mockito.verify(repository, Mockito.never()).deleteById(id);
    }

    @Test
    public void shouldBeUpdateBook(){
        Long id = 1L;

        Book updatingBook = createNewBookWithId();

        Book updatedBook = createNewBookWithId();
        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = bookService.update(updatingBook);

        MatcherAssert.assertThat(book.getId(), Matchers.equalTo(updatedBook.getId()));
        MatcherAssert.assertThat(book.getAuthor(), Matchers.equalTo(updatedBook.getAuthor()));
        MatcherAssert.assertThat(book.getIsbn(), Matchers.equalTo(updatedBook.getIsbn()));
        MatcherAssert.assertThat(book.getTitle(), Matchers.equalTo(updatedBook.getTitle()));
    }

    @Test
    public void shouldFailUpdateBook(){
        Book book = new Book();

        Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.update(book) );

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    public void shouldFoundBooks(){
        Book book = createNewBook();

        PageRequest pageRequest = PageRequest.of(0,100);

        List<Book> books = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(books, pageRequest, 1);
        Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = bookService.find(book, pageRequest);

        MatcherAssert.assertThat(result.getTotalElements(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(result.getContent(), Matchers.equalTo(books));
        MatcherAssert.assertThat(result.getPageable().getPageNumber(), Matchers.equalTo(0));
        MatcherAssert.assertThat(result.getPageable().getPageSize(), Matchers.equalTo(100));
    }
}

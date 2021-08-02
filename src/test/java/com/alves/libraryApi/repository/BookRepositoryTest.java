package com.alves.libraryApi.repository;

import com.alves.libraryApi.data.BookData;
import com.alves.libraryApi.model.Book;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    BookRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void returnTrueWhenIsbnExists(){
        String isbn = "0001";
        Book book = BookData.createNewBook(isbn);
        testEntityManager.persist(book);
        boolean exists = repository.existsByIsbn(isbn);
        MatcherAssert.assertThat(exists, Matchers.is(true));
    }

    @Test
    public void returnFalseWhenIsbnDoesntExists(){
        String isbn = "0001";
        boolean exists = repository.existsByIsbn(isbn);
        MatcherAssert.assertThat(exists, Matchers.is(false));
    }

    @Test
    public void shouldFoundBookById(){
        Book book = BookData.createNewBook();
        testEntityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        MatcherAssert.assertThat(foundBook.isPresent(), Matchers.is(true));
    }

    @Test
    public void shouldBeSaveBook(){
        Book book = BookData.createNewBook("123");

        Book savedBook = repository.save(book);

        MatcherAssert.assertThat(savedBook.getId(), Matchers.notNullValue());

    }

    @Test
    @Transactional
    public void shouldDeleteBook(){
        Book book = BookData.createNewBook("123");
        testEntityManager.persist(book);

        Book bookFound = testEntityManager.find(Book.class, book.getId());

        repository.delete(bookFound);

        Book deleteBook = testEntityManager.find(Book.class, book.getId());

        MatcherAssert.assertThat(deleteBook, Matchers.nullValue());
    }



}

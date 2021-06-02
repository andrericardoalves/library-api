package com.alves.libraryApi.repository;

import com.alves.libraryApi.model.Book;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
//@DataJdbcTest
public class BookRepositoryTest {

    @Autowired
    BookRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @Transactional
    public void returnTrueWhenIsbnExists(){
        String isbn = "0001";
        Book book = createNewBook(isbn);
        testEntityManager.persist(book);
        boolean exists = repository.existsByIsbn(isbn);
        MatcherAssert.assertThat(exists, Matchers.is(true));
    }

    @Test
    @Transactional
    public void returnFalseWhenIsbnDoesntExists(){
        String isbn = "0001";
        boolean exists = repository.existsByIsbn(isbn);
        MatcherAssert.assertThat(exists, Matchers.is(false));
    }


    public static Book createNewBook(String isbn) {
        return Book.builder().author("Andre").title("Programming for All").isbn(isbn).build();
    }

}

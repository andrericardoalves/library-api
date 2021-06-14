package com.alves.libraryApi.repository;

import com.alves.libraryApi.data.BookData;
import com.alves.libraryApi.data.CustomerData;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
//@DataJdbcTest
public class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @Transactional
    public void shouldSaveLoan(){

        Loan loan = createPersistenceLoan();

        List<Long> bookIds = loan.getBooks().stream().map(book -> book.getId()).collect(Collectors.toList());

        boolean exist = repository.existByBookAndNotReturned(1L);
        Long quantity = repository.quantityBookAndNotReturnedById(2L);
        List<Book> bookList = repository.searchBooksLoaned();

        Optional<Loan> bookFound = repository.findById(1L);

        MatcherAssert.assertThat(bookIds, Matchers.hasSize(2));
        MatcherAssert.assertThat(exist, Matchers.is(true));
        MatcherAssert.assertThat(quantity, Matchers.is(1L));
        MatcherAssert.assertThat(bookFound, Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(bookList, Matchers.hasSize(2));
    }

    @Transactional
    public Loan createPersistenceLoan(){
        Book bookOne = BookData.createNewBook();
       // testEntityManager.persist(bookOne);
        Book bookTwo = BookData.createNewBookTwo();
       // testEntityManager.persist(bookTwo);
        Customer customer = CustomerData.createCustomer();
        testEntityManager.persist(customer);


        Loan loan = Loan.builder()
                .books(Arrays.asList(bookOne,bookTwo))
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

       Loan loanSaved =  testEntityManager.persist(loan);

       return loanSaved;
    }


}

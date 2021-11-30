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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager testEntityManager;



    @Test
    public void shouldSaveLoan(){
        Loan loan = createPersistenceLoan();
        List<Long> bookIds = loan.getBooks().stream().map(Book::getId).collect(Collectors.toList());


        Long quantity = repository.quantityBookAndNotReturnedById(bookIds.get(0));
        List<Book> bookList = repository.searchBooksLoaned();

        Optional<Loan> bookFound = repository.findById(bookIds.get(0));

        MatcherAssert.assertThat(bookIds, Matchers.hasSize(2));

        MatcherAssert.assertThat(quantity, Matchers.is(1L));
        MatcherAssert.assertThat(bookFound, Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(bookList, Matchers.hasSize(2));
    }

    @Test
    public  void shouldExistByBookAndNotReturned(){

        createPersistenceLoan();
        boolean exist = repository.existByBookAndNotReturned(1L);
        MatcherAssert.assertThat(exist, Matchers.is(true));
    }

    @Test
    public void shouldFoundLoansByFilters(){
        createPersistenceLoan();
        Page<Loan> loans = repository.findLoanByFilters("Andre","Programming for All", PageRequest.of(0, 10));

        MatcherAssert.assertThat(loans.getContent(), Matchers.hasSize(1));
        MatcherAssert.assertThat(loans.getPageable().getPageSize(), Matchers.is(10));
        MatcherAssert.assertThat(loans.getPageable().getPageNumber(), Matchers.is(0));
        MatcherAssert.assertThat(loans.getTotalElements(), Matchers.is(1L));
    }


    public Loan createPersistenceLoan(){
        Book bookOne = BookData.createNewBook();
        testEntityManager.persist(bookOne);
        Book bookTwo = BookData.createNewBookTwo();
        testEntityManager.persist(bookTwo);
        Customer customer = CustomerData.createCustomer();
        testEntityManager.persist(customer);

        Loan loan = Loan.builder()
                .books(Arrays.asList(bookOne,bookTwo)) // ,
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

       return testEntityManager.persist(loan);

    }


}

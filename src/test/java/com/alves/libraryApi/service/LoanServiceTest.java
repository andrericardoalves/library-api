package com.alves.libraryApi.service;

import com.alves.libraryApi.data.BookData;
import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.repository.LoanRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    @Autowired
    LoanService service;

    @Test
    public void shouldSaveBook(){
        Book book = BookData.createNewBookWithId();
        Customer customer = Customer.builder().id(1L).build();

        Loan loan = Loan.builder()
                .customer(customer).
                books(Arrays.asList(book))
                .loanDate(LocalDate.now()).build();

        Loan loanSaved = Loan.builder()
                .id(1L)
                .customer(customer)
                .books(Arrays.asList(book))
                .loanDate(LocalDate.now()).build();

        Mockito.when( repository.existByBookAndNotReturned(book.getId()) ).thenReturn(false);
        Mockito.when( repository.save(loan) ).thenReturn(loanSaved);

        Loan loanFound = service.save(loan);

        MatcherAssert.assertThat(loanFound.getId(), Matchers.equalToObject(loanSaved.getId()));
        MatcherAssert.assertThat(loanFound.getBooks(), Matchers.equalTo(loanSaved.getBooks()));
        MatcherAssert.assertThat(loanFound.getCustomer().getId(), Matchers.equalToObject(loanSaved.getCustomer().getId()));
        MatcherAssert.assertThat(loanFound.getLoanDate(), Matchers.equalToObject(loanSaved.getLoanDate()));

    }

    @Test
    public void shouldReturnExceptionWhenTryLoadBookLoaned(){

      Book book = BookData.createNewBookWithId();
      Customer customer = Customer.builder().id(1L).build();

      Loan loan = Loan.builder()
            .customer(customer).
                    books(Arrays.asList(book))
            .loanDate(LocalDate.now()).build();

      Mockito.when( repository.existByBookAndNotReturned(book.getId()) ).thenReturn(true);

      Throwable throwable =  Assertions.assertThrows(BusinessException.class, ()-> service.save(loan));

      MatcherAssert.assertThat(throwable, CoreMatchers.instanceOf(BusinessException.class));
      Assertions.assertEquals( "Book already loaned." , throwable.getMessage());

      Mockito.verify(repository, Mockito.never()).save(loan);
    }

    @Test
    public void testListContains(){
        List<Boolean> listContainsTrue = Arrays.asList(false, false, true, false, true);
        List<Boolean> listNotContainsTrue = Arrays.asList(false, false, false, false, false);

        MatcherAssert.assertThat(listContainsTrue, IsCollectionWithSize.hasSize(5));
        MatcherAssert.assertThat(listContainsTrue, CoreMatchers.hasItem(true));
        MatcherAssert.assertThat(listNotContainsTrue, CoreMatchers.not(CoreMatchers.hasItem(true)));
    }
}

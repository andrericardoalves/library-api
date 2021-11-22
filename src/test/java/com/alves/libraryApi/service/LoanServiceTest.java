package com.alves.libraryApi.service;

import com.alves.libraryApi.data.BookData;
import com.alves.libraryApi.data.LoanData;
import com.alves.libraryApi.dto.LoanFilterDTO;
import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.repository.LoanRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void shouldFoundBookById(){
        Long id = 1L;
        Loan loan = LoanData.createNewLoanWithId();

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        MatcherAssert.assertThat(result.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(result.get().getId(), Matchers.equalTo(id));
        MatcherAssert.assertThat(result.get().getCustomer(), Matchers.equalTo(loan.getCustomer()));
        MatcherAssert.assertThat(result.get().getBooks(), Matchers.equalTo(loan.getBooks()));
        MatcherAssert.assertThat(result.get().getLoanDate(), Matchers.equalTo(loan.getLoanDate()));

        Mockito.verify(repository).findById(id);
    }

    @Test
    public void shouldUpdateLoanToReturned(){
        Loan loan = LoanData.createNewLoanWithId();
        loan.setReturned(true);

        Mockito.when(repository.save(loan)).thenReturn(loan);

        Loan updateLoan = service.update(loan);

        MatcherAssert.assertThat(updateLoan.getReturned(), Matchers.is(true));
        Mockito.verify(repository).save(loan);

    }

    @Test
    public void shouldFoundLoans(){
        LoanFilterDTO filterDTO = LoanFilterDTO.builder()
                .author("Andre")
                .title("Programming for All").build();

        Loan loan = LoanData.createNewLoanWithId();
        List<Loan> loans = Arrays.asList(loan);
        PageRequest pageRequest = PageRequest.of(0,100);

        Page<Loan> page = new PageImpl<Loan>(loans, pageRequest, loans.size());
        Mockito.when( repository.findLoanByFilters(
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.any(PageRequest.class)))
                .thenReturn(page);
        // Aula 75 8:34
        Page<Loan> result = service.findLoanByFilters(filterDTO, pageRequest);

        MatcherAssert.assertThat(result.getTotalElements(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(result.getContent(), Matchers.equalTo(loans));
        MatcherAssert.assertThat(result.getPageable().getPageNumber(), Matchers.equalTo(0));
        MatcherAssert.assertThat(result.getPageable().getPageSize(), Matchers.equalTo(100));
    }

    @Test
    public void shouldReturnLateLoan(){
        LocalDate fourDaysBefore = LocalDate.now().minusDays(3);
        Loan loan = LoanData.createNewLoanWithId();
        loan.setLoanDate(fourDaysBefore);
        List<Loan> loanList = Arrays.asList(loan);

        Mockito.when(repository.findByLoanDateLessThanAndNotReturned(fourDaysBefore)).thenReturn(loanList);

        List<Loan> lateLoans =  service.findByLoanDateLessThanAndNotReturned(fourDaysBefore);

        MatcherAssert.assertThat(lateLoans.size(), Matchers.equalTo(1));
    }

    @Test
    public void shouldNotFoundLateLoan(){
       LocalDate fourDaysBefore = LocalDate.now().minusDays(3);
        Loan loan = LoanData.createNewLoanWithId();
        loan.setLoanDate(fourDaysBefore);
        loan.setReturned(true);
        List<Loan> loanList = Arrays.asList(loan);

      Mockito.when(repository.findByLoanDateLessThanAndNotReturned(fourDaysBefore))
               .thenReturn(Arrays.asList());

       List<Loan> lateLoans =  service.findByLoanDateLessThanAndNotReturned(fourDaysBefore);

       MatcherAssert.assertThat(lateLoans.size(), Matchers.equalTo(0));
    }
}

package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.LoanDTO;
import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.service.BookService;
import com.alves.libraryApi.service.CustomerService;
import com.alves.libraryApi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanResource.class)
public class LoanResourceTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private LoanService loanService;

    @Test
    public void shouldBeCreateLoan() throws Exception {
        Customer customer = Customer.builder().id(1L).build();

        LoanDTO loanDto = LoanDTO.builder().idBooks(Arrays.asList(1L))
                .idCustomer(customer.getId()).build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Book book = Book.builder().id(1L).author("Andre").title("I love programing").isbn("123").build();
        BDDMockito.given( bookService.getById(1L)).willReturn(Optional.of(book));

        BDDMockito.given( customerService.findById(1L)).willReturn(Optional.of(customer));

        Loan loan = Loan.builder().id(1L)
                .customer(customer)
                .books(Arrays.asList(book))
                .loanDate(LocalDate.now()).build();

        BDDMockito.given( loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    public void shouldBeCreateLoanMoreOneBook() throws Exception {
        Customer customer = Customer.builder().id(1L).build();

        LoanDTO loanDto = LoanDTO.builder().idBooks(Arrays.asList(1L, 2L))
                .idCustomer(customer.getId()).build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Book bookOne = Book.builder().id(1L).author("Andre").title("I love programing").isbn("123").build();
        BDDMockito.given( bookService.getById(1L)).willReturn(Optional.of(bookOne));

        Book bookTwo = Book.builder().id(2L).author("Ingrid").title("Programing for All").isbn("456").build();
        BDDMockito.given( bookService.getById(2L)).willReturn(Optional.of(bookTwo));

        BDDMockito.given( customerService.findById(1L)).willReturn(Optional.of(customer));

        Loan loan = Loan.builder().id(1L)
                .customer(customer)
                .books(Arrays.asList(bookOne, bookTwo))
                .loanDate(LocalDate.now()).build();

        BDDMockito.given( loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    public void shouldReturnExceptionBookNoExistent() throws  Exception{

        Customer customer = Customer.builder().id(1L).build();

        LoanDTO loanDto = LoanDTO.builder().idBooks(Arrays.asList(1L))
                .idCustomer(customer.getId()).build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Book bookOne = Book.builder().id(1L).author("Andre").title("I love programing").isbn("123").build();
        BDDMockito.given( bookService.getById(1L)).willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Book not found by id."));
    }



    @Test
    public void shouldReturnErrorBookLoaned() throws Exception {
        Customer customer = Customer.builder().id(1L).build();

        LoanDTO loanDto = LoanDTO.builder().idBooks(Arrays.asList(1L))
                .idCustomer(customer.getId()).build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        Book book = Book.builder().id(1L).author("Andre").title("I love programing").isbn("123").build();
        BDDMockito.given( bookService.getById(1L)).willReturn(Optional.of(book));

        BDDMockito.given( customerService.findById(1L)).willReturn(Optional.of(customer));

        BDDMockito.given( loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned."));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Book already loaned."));
    }
}


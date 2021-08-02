package com.alves.libraryApi.resource;

import com.alves.libraryApi.data.BookData;
import com.alves.libraryApi.data.LoanData;
import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.dto.LoanDTO;
import com.alves.libraryApi.dto.LoanFilterDTO;
import com.alves.libraryApi.dto.ReturnedLoanDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import static com.alves.libraryApi.resource.BookResourceTest.BOOK_API;

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
        BookDTO dto = BookData.createNewBookDTOWithId();
        LoanDTO loanDto = LoanDTO.builder().booksDTO(Arrays.asList(dto))
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

        BookDTO dto = BookData.createNewBookDTOWithId();
        LoanDTO loanDto = LoanDTO.builder().booksDTO(Arrays.asList(dto))
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

        BookDTO dto = BookData.createNewBookDTOWithId();
        LoanDTO loanDto = LoanDTO.builder().booksDTO(Arrays.asList(dto))
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

        BookDTO dto = BookData.createNewBookDTOWithId();
        LoanDTO loanDto = LoanDTO.builder().booksDTO(Arrays.asList(dto))
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

    @Test
    public void shouldReturnBookLoaned() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = LoanData.createNewLoanWithId();
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder  request = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    public void shouldFailReturnLoanNotExist() throws Exception {
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());



        MockHttpServletRequestBuilder  request = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void shouldReturnListOfBooks() throws Exception {
        Loan loan = LoanData.createNewLoanWithId();

        BDDMockito.given( loanService.findLoanByFilters(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&customer=%s&author=%s&page=%s&size=%s",
                loan.getBooks().get(0).getTitle(), loan.getCustomer().getName(),
                loan.getBooks().get(0).getAuthor() ,0, 100);

        MockHttpServletRequestBuilder  request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect( MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect( MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));

    }
}


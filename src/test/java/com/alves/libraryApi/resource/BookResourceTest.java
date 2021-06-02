package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.service.BookService;
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

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookResourceTest {

    static  String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    public void shouldBeCreateBook() throws Exception {

        BookDTO dto = createNewBook();
        Book bookSaved = Book.builder().id(1L).author("Andre").title("Programming for All").isbn("0001").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(bookSaved);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
           .perform(request)
           .andExpect( MockMvcResultMatchers.status().isCreated() )
           .andExpect( MockMvcResultMatchers.jsonPath("id").isNotEmpty())
           .andExpect( MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()) )
           .andExpect( MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()) )
           .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()) );
    }

    @Test
    public void shouldFailMessageCreateBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    public void shouldReturnErrorDuplicatedIsbn() throws Exception {
        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String message = "Isbn already exist";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(message));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
           .andExpect( MockMvcResultMatchers.status().isBadRequest())
           .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
           .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(message));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Andre").title("Programming for All").isbn("0001").build();
    }
}

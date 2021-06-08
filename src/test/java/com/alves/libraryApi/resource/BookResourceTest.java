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
import org.mockito.Mock;
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

import java.util.Arrays;
import java.util.Optional;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(controllers = BookResourceTest.class)
@AutoConfigureMockMvc
public class BookResourceTest {

    static  String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    public void shouldBeCreateBook() throws Exception {

        BookDTO dto = createNewBookDTO();
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
        BookDTO dto = createNewBookDTO();
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

    @Test
    public void searchBookDetails() throws Exception {
        Long id = 1L;
        Book book = createNewBook();
        book.setId(id);

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(id) )
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(book.getTitle()) )
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()) );
    }

    @Test
    public void BookNotFound() throws Exception {
       BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
       MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldBeDeleteBook() throws Exception {
       BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void shouldDeleteBookNotFound() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldUpdateBook() throws Exception {
        Long id = 1L;
        BookDTO bookDTO = BookDTO.builder().author("Ingrid Martins").title("I love to travel around the world").isbn("0002").build();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        Book updatingBook = Book.builder()
                .id(id).author("Ingrid").title("I love to travel").isbn("0002").build();
        BDDMockito.given( service.getById(id)).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder()
                .id(id).author("Ingrid Martins").title("I love to travel around the world").isbn("0002").build();
        BDDMockito.given( service.update(updatedBook) ).willReturn(updatedBook);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(id) )
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()) )
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()) );
    }


    @Test
    public void shouldUpdateBookNotFound() throws Exception {
        BookDTO dto = createNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given( service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void shouldBeReturnListOfBooks() throws Exception {
        Book book = createNewBookWithId();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=%s&size=%s",
                book.getTitle(), book.getAuthor(),0, 100);

        MockHttpServletRequestBuilder  request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
           .andExpect( MockMvcResultMatchers.status().isOk() )
           .andExpect( MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
           .andExpect( MockMvcResultMatchers.jsonPath("totalElements").value(1))
           .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
           .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }

    private Book createNewBookWithId() {
        return Book.builder()
                .id(1L).author("Andre").title("Programming for All").isbn("0001").build();
    }

    private Book createNewBook() {
        return Book.builder()
                .author("Andre").title("Programming for All").isbn("0001").build();
    }

    private BookDTO createNewBookDTO() {
        return BookDTO.builder()
                .author("Andre").title("Programming for All").isbn("0001").build();
    }
}

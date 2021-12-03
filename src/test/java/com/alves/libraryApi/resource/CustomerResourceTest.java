package com.alves.libraryApi.resource;

import com.alves.libraryApi.data.CustomerData;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.service.CustomerService;
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

import java.util.Arrays;
import java.util.Optional;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@WebMvcTest(controllers = CustomerResource.class)
@AutoConfigureMockMvc
public class CustomerResourceTest {

    static  String CUSTOMERS_API = "/api/customers";

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomerService service;

    @Test
    public void shouldSaveCustomer() throws Exception {
        Customer customer = Customer.builder()
                .id(1L).name("John").surName("White").email("johnwhite@gmail.com").build();
        String json = new ObjectMapper().writeValueAsString(customer);

        BDDMockito.given(service.save(Mockito.any(Customer.class))).willReturn(customer);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMERS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(customer.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("surName").value(customer.getSurName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(customer.getEmail()));
    }

    @Test
    public void shouldUpdateCustomer() throws Exception {
        Customer customer = Customer.builder()
                .id(1L).name("John").surName("White").email("johnwhite@gmail.com").build();
        String json = new ObjectMapper().writeValueAsString(customer);

        BDDMockito.given(service.update(Mockito.any(Customer.class))).willReturn(customer);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(CUSTOMERS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(customer.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(customer.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("surName").value(customer.getSurName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(customer.getEmail()));
    }


    @Test
    public void shouldFoundCustomerByEmail() throws Exception {
        String email = "johnwhite@gmail.com";
        Customer customer = CustomerData.createCustomer();

        BDDMockito.given(service.findByEmail(email)).willReturn(Optional.of(customer));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMERS_API.concat("/findByEmail"))
                .param("email", email)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(customer.getId()) )
                .andExpect( MockMvcResultMatchers.jsonPath("name").value(customer.getName()) )
                .andExpect( MockMvcResultMatchers.jsonPath("email").value(customer.getEmail()) );
    }

    @Test
    public void findByEmailCustomerNotFound() throws Exception {
        String email = "johnwhite@gmail.com";

        BDDMockito.given(service.findByEmail(email)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMERS_API.concat("/findByEmail"))
                .param("email", email)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findByIdCustomerNotFound() throws Exception {
        Long id = 1L;
        Customer customer = CustomerData.createCustomer();

         BDDMockito.given(service.findById(id)).willReturn(Optional.of(customer));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMERS_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(customer.getId()) )
                .andExpect( MockMvcResultMatchers.jsonPath("name").value(customer.getName()) )
                .andExpect( MockMvcResultMatchers.jsonPath("email").value(customer.getEmail()) );
    }


    @Test
    public void shouldCustomerNotFound() throws Exception {
        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMERS_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnListCostumers() throws Exception {
        Customer customer = CustomerData.createCustomer();

        BDDMockito.given( service.find(Mockito.any(Customer.class),Mockito.any(Pageable.class)))
           .willReturn(new PageImpl<Customer>(Arrays.asList(customer), PageRequest.of(0, 100), 1));

        String queryString = String.format("?name=%s&page=%s&size=%s", customer.getName(), 0, 100);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMERS_API.concat(queryString))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
            .andExpect( MockMvcResultMatchers.status().isOk() )
            .andExpect( MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
            .andExpect( MockMvcResultMatchers.jsonPath("totalElements").value(1))
            .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
            .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }


}

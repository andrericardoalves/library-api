package com.alves.libraryApi.resource;

import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.service.CustomerService;
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
    public void shouldFoundCustomer() throws Exception {
        Long id = 1L;
        Customer customer = createCustomer();

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

    private Customer createCustomer(){
        return Customer.builder().id(1L).name("Andre").email("andre@email.com").build();
    }
}

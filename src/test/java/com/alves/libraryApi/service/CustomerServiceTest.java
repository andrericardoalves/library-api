package com.alves.libraryApi.service;

import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.repository.CustomerRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
public class CustomerServiceTest {

    @Autowired
    private CustomerService service;

    @MockBean
    private CustomerRepository repository;

    @Test
    public void shouldBeFoundBook(){
        Long id = 1L;
        Customer customer = createCustomer();
        Mockito.when( service.findById(id)).thenReturn(Optional.of(customer));

        Optional<Customer> customerFound = service.findById(id);

        MatcherAssert.assertThat(customerFound.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(customerFound.get().getId(), Matchers.equalTo(id));
        MatcherAssert.assertThat(customerFound.get().getName(), Matchers.equalTo(customer.getName()));
        MatcherAssert.assertThat(customerFound.get().getEmail(), Matchers.equalTo(customer.getEmail()));
    }


    @Test
    public void shouldNotFoundCustomerById(){
        Long id = 1L;
        Mockito.when( repository.findById(id) ).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = repository.findById(id);

        MatcherAssert.assertThat(foundCustomer.isPresent(), Matchers.is(false));

    }


    private Customer createCustomer(){
        return Customer.builder().id(1L).name("Andre").email("andre@email.com").build();
    }
}

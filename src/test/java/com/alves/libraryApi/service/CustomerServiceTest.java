package com.alves.libraryApi.service;

import com.alves.libraryApi.data.CustomerData;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
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
        Customer customer = CustomerData.createCustomerWithId();
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

    @Test
    public void shouldFoundCustomers(){
        Customer customer = CustomerData.createCustomer();

        PageRequest pageRequest = PageRequest.of(0,100);

        List<Customer> customers = Arrays.asList(customer);
        Page<Customer> page = new PageImpl<>(customers , pageRequest, 1 );

        Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Customer> result = service.find(customer, pageRequest);

        MatcherAssert.assertThat(result.getTotalElements(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(result.getContent(), Matchers.equalTo(customers));
        MatcherAssert.assertThat(result.getPageable().getPageNumber(), Matchers.equalTo(0));
        MatcherAssert.assertThat(result.getPageable().getPageSize(), Matchers.equalTo(100));
    }

}

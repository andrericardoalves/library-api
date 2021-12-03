package com.alves.libraryApi.service;

import com.alves.libraryApi.data.CustomerData;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.repository.CustomerRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
    public void shouldSaveCustomerWithSuccess() throws Exception {
        Customer customerParam = CustomerData.createCustomer();
        Customer customerReturn = CustomerData.createCustomerWithId();
        String email = "johnwhite@gmail.com";

        Mockito.when( repository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when( repository.save(Mockito.any())).thenReturn(customerReturn);

        Customer customer = service.save(customerParam);

        MatcherAssert.assertThat(customer.getId(), Matchers.equalTo(customerReturn.getId()));
        MatcherAssert.assertThat(customer.getName(), Matchers.equalTo(customerReturn.getName()));
        MatcherAssert.assertThat(customer.getEmail(), Matchers.equalTo(customerReturn.getEmail()));
    }

    @Test
    public void shouldUpdateCustomerWithSuccess() throws Exception {

        Customer customerSaved = Customer.builder().id(1L).name("And").surName("Alv").email("andre@email.com.br").build();
        Customer customerUpdated = Customer.builder().id(1L).name("Andre").surName("Ricardo Alves").email("andre@email.com").build();

        Mockito.when( service.findById(Mockito.anyLong())).thenReturn(Optional.of(customerSaved));
        Mockito.when( service.update(customerUpdated)).thenReturn(customerUpdated);

        Customer customer = service.update(customerUpdated);

        MatcherAssert.assertThat(customer.getId(), Matchers.equalTo(customerUpdated.getId()));
        MatcherAssert.assertThat(customer.getName(), Matchers.equalTo(customerUpdated.getName()));
        MatcherAssert.assertThat(customer.getSurName(), Matchers.equalTo(customerUpdated.getSurName()));
        MatcherAssert.assertThat(customer.getEmail(), Matchers.equalTo(customerUpdated.getEmail()));
    }

    @Test
    public void shouldNotUpdateCustomerNotFound() {
        Customer customerUpdated = Customer.builder().id(1L).name("Andre").surName("Ricardo Alves").email("andre@email.com").build();

        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> service.update(customerUpdated));

        Assertions.assertEquals("Customer no found: " + customerUpdated.getId() , exception.getMessage());
    }

    @Test
    public void shouldNotSaveCustomerWithEmailExist(){
        Customer customerParam = CustomerData.createCustomerWithId();
        String email = "andre@email.com";

        Mockito.when( service.findByEmail(email)).thenReturn(Optional.of(customerParam));

        Exception exception = Assertions.assertThrows(Exception.class, () -> service.save(customerParam));

        Assertions.assertEquals("Already customer with email: " + customerParam.getEmail() , exception.getMessage());
    }

    @Test
    public void shouldDeleteCustomerSuccess() throws Exception {
        Long id = 1L;
        Customer customer = CustomerData.createCustomerWithId();

        Mockito.when( service.findById(id)).thenReturn(Optional.of(customer));
        Mockito.doNothing().when(repository).deleteById(id);

        service.deleteById(id);

        Mockito.verify(repository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void shouldFailWhenDeleteCustomerNotExist() throws Exception {
        Long id = 1L;

        Mockito.when( service.findById(id)).thenReturn(Optional.empty());
        Mockito.doNothing().when(repository).deleteById(id);

        Exception exception = Assertions.assertThrows(Exception.class, () -> service.deleteById(id));

        Assertions.assertEquals("Customer not found: " + id , exception.getMessage());
    }

    @Test
    public void shouldFindCustomerByEmail(){
        String email = "johnwhite@gmail.com";
        Customer customer = CustomerData.createCustomerWithId();

        Mockito.when( service.findByEmail(email)).thenReturn(Optional.of(customer));
        Optional<Customer> customerFound = service.findByEmail(email);

        MatcherAssert.assertThat(customerFound.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(customerFound.get().getId(), Matchers.equalTo(customer.getId()));
        MatcherAssert.assertThat(customerFound.get().getName(), Matchers.equalTo(customer.getName()));
        MatcherAssert.assertThat(customerFound.get().getEmail(), Matchers.equalTo(customer.getEmail()));
    }


    @Test
    public void shouldNotFoundCustomerByEmail(){
        String email = "johnwhite@gmail.com";

        Mockito.when( repository.findByEmail(email)).thenReturn(Optional.empty());
        Optional<Customer> foundCustomer = repository.findByEmail(email);

        MatcherAssert.assertThat(foundCustomer.isPresent(), Matchers.is(false));

    }

    @Test
    public void shouldFoundBookById(){
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

        List<Customer> customers = List.of(customer);
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

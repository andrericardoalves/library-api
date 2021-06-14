package com.alves.libraryApi.repository;

import com.alves.libraryApi.data.CustomerData;
import com.alves.libraryApi.model.Customer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestEntityManager
//@DataJdbcTest
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @Transactional()
    public void shouldFoundCostumerById(){
        Customer customer = CustomerData.createCustomer();
        testEntityManager.persist(customer);

        Optional<Customer> foundCustomer = repository.findById(1L);

        MatcherAssert.assertThat(foundCustomer.isPresent(), Matchers.is(true));
    }


}

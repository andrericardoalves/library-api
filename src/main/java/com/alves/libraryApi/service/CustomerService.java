package com.alves.libraryApi.service;

import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    public Page<Customer> find(Customer customer, Pageable pageable) {
        Example<Customer>  example = Example.of(customer,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );

        return repository.findAll(example, pageable);
    }

    public Customer save(Customer customer) throws Exception {

        Optional<Customer> customerSaved = findByEmail(customer.getEmail());

        if(customerSaved.isPresent()){
            throw new Exception("Already customer with email: " + customer.getEmail() );
        }

        return repository.save(customer);
    }

    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Customer update(Customer customer) throws Exception {
        Optional<Customer> customerSaved = findById(customer.getId());

        if(!customerSaved.isPresent()){
            throw new Exception("Customer no found: " + customer.getId());
        }
       return repository.save(customer);
    }

    public void deleteById(Long id) throws Exception {

        Optional<Customer> customerSaved = findById(id);

        if(!customerSaved.isPresent()){
            throw new Exception("Customer not found: " + id);
        }

        repository.deleteById(id);
    }
}

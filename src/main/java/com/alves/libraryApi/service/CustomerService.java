package com.alves.libraryApi.service;

import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }
}

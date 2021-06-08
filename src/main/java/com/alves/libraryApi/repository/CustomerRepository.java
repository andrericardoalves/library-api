package com.alves.libraryApi.repository;

import com.alves.libraryApi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

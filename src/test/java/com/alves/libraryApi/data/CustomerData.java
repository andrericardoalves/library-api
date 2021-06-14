package com.alves.libraryApi.data;

import com.alves.libraryApi.dto.CustomerDTO;
import com.alves.libraryApi.model.Customer;

public class CustomerData {

    public static Customer createCustomer(){
        return Customer.builder().name("Andre").email("andre@email.com").build();
    }

    public static Customer createCustomerWithId(){
        return Customer.builder().id(1L).name("Andre").email("andre@email.com").build();
    }

    public static CustomerDTO createCustomerDTO(){
        return CustomerDTO.builder().id(1L).name("Andre").email("andre@email.com").build();
    }

  }

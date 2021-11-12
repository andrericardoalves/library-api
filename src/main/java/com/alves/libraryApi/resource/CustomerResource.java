package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.CustomerDTO;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerResource {

    @Autowired
    private CustomerService service;

    ModelMapper modelMapper;

    public CustomerResource(){ modelMapper = new ModelMapper();}


    @GetMapping("{id}")
    public CustomerDTO findById(@PathVariable Long id){
      return service.findById(id)
               .map( customer -> {
                    return modelMapper.map(customer, CustomerDTO.class);
                   }
               ).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<CustomerDTO> find(CustomerDTO customerDTO, Pageable pageable){
        Customer customerFilter = modelMapper.map(customerDTO, Customer.class);
        Page<Customer> result = service.find(customerFilter, pageable);
        List<CustomerDTO> list = result.getContent()
                .stream()
                .map( entity -> modelMapper.map(entity, CustomerDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

}

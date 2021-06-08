package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.CustomerDTO;
import com.alves.libraryApi.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

}

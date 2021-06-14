package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.LoanDTO;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.service.BookService;
import com.alves.libraryApi.service.LoanService;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanResource {

    @Autowired
    private LoanService service;
    @Autowired
    private BookService bookService;

    private ModelMapper modelMapper;

    public LoanResource() { modelMapper = new ModelMapper(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        List<Book> books =
                  dto.getIdBooks()
                .stream()
                .map( idBook -> {
                 return bookService.getById(idBook)
                            .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found by id."));
                }).collect(Collectors.toList());

        Loan entity = Loan.builder()
            .books(books)
            .customer(Customer.builder().id(dto.getIdCustomer()).build())
            .loanDate(LocalDate.now())
            .build();

        entity = service.save(entity);
        return entity.getId();
    }
}



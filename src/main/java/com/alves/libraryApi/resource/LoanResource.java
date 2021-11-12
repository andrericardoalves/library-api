package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.dto.LoanDTO;
import com.alves.libraryApi.dto.LoanFilterDTO;
import com.alves.libraryApi.dto.ReturnedLoanDTO;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.service.BookService;
import com.alves.libraryApi.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
                  dto.getBooksDTO()
                .stream()
                .map( bookDTO -> {
                 return bookService.getById(bookDTO.getId())
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

    @PatchMapping("{id}")
    public void returnBooks(@PathVariable Long id, @RequestBody ReturnedLoanDTO loanDTO){
        Loan loan = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(loanDTO.getReturned());
        service.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO loanFilterDTO, Pageable pageable){
        Page<Loan> loans = service.findLoanByFilters(loanFilterDTO, pageable);
        List<LoanDTO> loansDTO = loans
                                 .getContent()
                                 .stream()
                             .map(entity -> {
                               LoanDTO loanDTO =  modelMapper.map(entity, LoanDTO.class);
                               List<BookDTO> booksDTO = entity.getBooks().stream()
                                                       .map(b -> modelMapper.map(b, BookDTO.class))
                                                       .collect(Collectors.toList());
                               loanDTO.setBooksDTO(booksDTO);
                               return loanDTO;
                             }
                             )
                             .collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loansDTO,pageable, loans.getTotalElements());
    }
}



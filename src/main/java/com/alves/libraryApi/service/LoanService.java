package com.alves.libraryApi.service;

import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository repository;

    public Loan save(Loan loan) {

        boolean loaned =
                loan.getBooks()
                        .stream()
                        .map( book -> { return repository.existByBookAndNotReturned(book.getId()); })
                        .collect(Collectors.toList())
                        .contains(true);
        if(loaned){
            throw new BusinessException("Book already loaned.");
        }

        return repository.save(loan);
    }
}


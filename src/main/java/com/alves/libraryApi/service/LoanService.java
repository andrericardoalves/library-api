package com.alves.libraryApi.service;

import com.alves.libraryApi.dto.LoanFilterDTO;
import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Loan;
import com.alves.libraryApi.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    public Page<Loan> findLoanByFilters(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        return repository.findLoanByFilters(loanFilterDTO.getAuthor(), loanFilterDTO.getTitle(), pageable);
    }

    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

    public List<Loan> findByLoanDateLessThanAndNotReturned(LocalDate threeDaysAgo ){
        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }
}


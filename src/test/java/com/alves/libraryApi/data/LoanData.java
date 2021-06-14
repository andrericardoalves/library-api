package com.alves.libraryApi.data;

import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Customer;
import com.alves.libraryApi.model.Loan;

import java.time.LocalDate;
import java.util.Arrays;

public class LoanData {

    public static Loan createNewLoan(){
        Book book = BookData.createNewBookWithId();
        Customer customer = Customer.builder().id(1L).build();

        Loan loan = Loan.builder()
                .customer(customer)
                .books(Arrays.asList(book))
                .loanDate(LocalDate.now()).build();
        return loan;
    }

    public static Loan createNewLoanWithId(){
        Book book = BookData.createNewBookWithId();
        Customer customer = Customer.builder().id(1L).build();

        Loan loan = Loan.builder()
                .id(1L)
                .customer(customer).
                        books(Arrays.asList(book))
                .loanDate(LocalDate.now()).build();
        return loan;
    }
}

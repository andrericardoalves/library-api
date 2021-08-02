package com.alves.libraryApi.repository;

import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(" select case when (count(b) > 0) then true else false end " +
            " from Loan l " +
            " left join l.books b " +
            "  where 1 = 1 " +
            "   and b.id = :bookId" +
            "   and ( l.returned is null or l.returned is false  )"
    )
    boolean existByBookAndNotReturned(@Param("bookId") Long bookId);

    @Query(" select count(l.id) " +
            " from Loan l " +
            " left join l.books b " +
            "  where 1 = 1 " +
            "   and b.id in :bookId" +
            "   and ( l.returned is null or l.returned is false  )"
    )
    Long quantityBookAndNotReturnedById(@Param("bookId") Long bookId);

    @Query("select distinct l.books from Loan l join l.books b ")
    List<Book> searchBooksLoaned();

    @Query(" select l " +
            " from Loan l " +
            " join l.books b " +
            " join l.customer c " +
            " where 1 = 1 " +
            " and ( b.author = :author or b.title = :title ) " +
            " ")
    Page<Loan> findLoanByFilters( @Param("author") String author, @Param("title") String title, Pageable pageable);
}

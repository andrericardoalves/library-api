package com.alves.libraryApi.repository;

import com.alves.libraryApi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> getByIsbn(String isbn);
}

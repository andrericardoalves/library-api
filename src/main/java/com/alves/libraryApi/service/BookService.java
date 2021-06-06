package com.alves.libraryApi.service;

import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository repository;

    public Book save(Book book){
        if(repository.existsByIsbn(book.getIsbn())){
            throw  new BusinessException("Isbn already exist");
        }
        return repository.save(book);
    }

    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id cant be null");
        }
        repository.deleteById(id);
    }

    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Id cant be null");
        }
        return repository.save(book);
    }

    public Page<Book> find(Book book, Pageable pageable) {
        Example<Book> example = Example.of(book,
                ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );

        return repository.findAll(example, pageable);
    }
}

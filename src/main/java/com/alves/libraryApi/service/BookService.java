package com.alves.libraryApi.service;

import com.alves.libraryApi.exception.BusinessException;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

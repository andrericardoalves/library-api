package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/books")
public class BookResource {

   @Autowired
   private BookService service;

   private ModelMapper modelMapper;

   public  BookResource(){
      modelMapper = new ModelMapper();
   }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){
        Book bookEntity = modelMapper.map(bookDTO, Book.class);
        bookEntity = service.save(bookEntity);
        return modelMapper.map(bookEntity, BookDTO.class);
    }
}

package com.alves.libraryApi.resource;

import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.model.Book;
import com.alves.libraryApi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


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

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
       return service.getById(id)
               .map( book -> modelMapper.map(book, BookDTO.class))
               .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id ){
       service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
       service.delete(id);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id , @RequestBody @Valid BookDTO bookDTO){
       return service.getById(id)
                .map( book -> {
                    book.setAuthor(bookDTO.getAuthor());
                    book.setTitle(bookDTO.getTitle());
                    book = service.update(book);
                    return modelMapper.map(book, BookDTO.class);
                  }
               )
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable){
       Book bookFilter = modelMapper.map(bookDTO, Book.class);
       Page<Book> result = service.find(bookFilter, pageable);
       List<BookDTO> list = result.getContent()
               .stream()
               .map( entity -> modelMapper.map(entity, BookDTO.class))
               .collect(Collectors.toList());
       return new PageImpl<BookDTO>(list, pageable, result.getTotalElements());
   }

}

package com.alves.libraryApi.data;

import com.alves.libraryApi.dto.BookDTO;
import com.alves.libraryApi.model.Book;

public class BookData {

    public static Book createNewBook() {
        return Book.builder().author("Andre").title("Programming for All").isbn("0001").build();
    }

    public static Book createNewBookTwo() {
        return Book.builder().author("Ingrid").title("I love programing").isbn("0002").build();
    }

    public static Book createNewBookWithId() {
        return Book.builder().id(1L).author("Andre").title("Programming for All").isbn("0001").build();
    }

    public static Book createNewBook(String isbn) {
        return Book.builder().author("Andre").title("Programming for All").isbn(isbn).build();
    }

     public static BookDTO createNewBookDTO() {
        return BookDTO.builder()
                .author("Andre").title("Programming for All").isbn("0001").build();
    }
}

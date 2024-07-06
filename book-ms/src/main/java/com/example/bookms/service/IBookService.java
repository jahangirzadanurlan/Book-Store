package com.example.bookms.service;

import com.example.bookms.model.dto.request.BookRequestDto;
import com.example.bookms.model.entity.Book;
import com.example.bookms.model.entity.OrderedBooks;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBookService {
    ResponseEntity<String> saveBook(BookRequestDto bookRequestDto);

    ResponseEntity<String> deleteBook(Long id);

    ResponseEntity<List<Book>> getAll();

    ResponseEntity<Book> getBookById(Long id);

    ResponseEntity<List<Book>> getBooksByCategoryName(String name);

    ResponseEntity<List<Book>> getBooksByName(String name);

    ResponseEntity<String> buyBook(Long id, String token);

    ResponseEntity<List<Book>> getOrders(String token);
}

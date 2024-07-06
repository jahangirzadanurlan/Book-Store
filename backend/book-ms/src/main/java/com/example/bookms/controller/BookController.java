package com.example.bookms.controller;

import com.example.bookms.model.entity.Book;
import com.example.bookms.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/common")
public class BookController {
    private final IBookService bookService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Book>> getAllBooks(){
        return bookService.getAll();
    }

    @GetMapping("/getAllByCategoryName/{name}")
    public ResponseEntity<List<Book>> getBooksByCategoryName(@PathVariable(value = "name") String name){
        return bookService.getBooksByCategoryName(name);
    }

    @GetMapping("/getBooksByName/{name}")
    public ResponseEntity<List<Book>> getBooksByName(@PathVariable(value = "name") String name){
        return bookService.getBooksByName(name);
    }

    @GetMapping("/getBookById/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable(value = "id") Long id){
        return bookService.getBookById(id);
    }
}

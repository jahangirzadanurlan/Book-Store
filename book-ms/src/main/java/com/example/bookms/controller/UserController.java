package com.example.bookms.controller;

import com.example.bookms.model.entity.Book;
import com.example.bookms.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final IBookService bookService;

    @PostMapping("/buy/{id}")
    public ResponseEntity<String> buyBook(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @PathVariable(value = "id") Long id){
        return bookService.buyBook(id,token);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Book>> getOrders(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        return bookService.getOrders(token);
    }
}

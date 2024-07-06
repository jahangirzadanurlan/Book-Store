package com.example.bookms.controller;

import com.example.bookms.model.dto.request.BookRequestDto;
import com.example.bookms.model.dto.request.CategoryRequestDto;
import com.example.bookms.service.IBookService;
import com.example.bookms.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final IBookService bookService;
    private final ICategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createBook(@Valid @RequestBody BookRequestDto bookRequestDto){
        return bookService.saveBook(bookRequestDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable(value = "id") Long id){
        return bookService.deleteBook(id);
    }

    @PostMapping("/create-category")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        return categoryService.saveCategory(categoryRequestDto);
    }

    @DeleteMapping("/delete-category/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(value = "id") Long id){
        return categoryService.deleteCategory(id);
    }

}

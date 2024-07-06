package com.example.bookms.service;

import com.example.bookms.model.dto.request.CategoryRequestDto;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {

    ResponseEntity<String> saveCategory(CategoryRequestDto categoryRequestDto);

    ResponseEntity<String> deleteCategory(Long id);
}

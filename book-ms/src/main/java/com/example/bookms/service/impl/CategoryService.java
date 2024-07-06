package com.example.bookms.service.impl;

import com.example.bookms.model.dto.request.CategoryRequestDto;
import com.example.bookms.model.entity.Book;
import com.example.bookms.model.entity.Category;
import com.example.bookms.repository.BookRepository;
import com.example.bookms.repository.CategoryRepository;
import com.example.bookms.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ResponseEntity<String> saveCategory(CategoryRequestDto categoryRequestDto) {
        Category category = Category.builder()
                .name(categoryRequestDto.getName())
                .build();
        categoryRepository.save(category);
        return ResponseEntity.ok().body("Category saved");
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteCategory(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            // Kategoriye bağlı kitapları alıyoruz
            List<Book> books = category.getBooks();

            // Kitapları tek tek siliyoruz
            for (Book book : books) {
                bookRepository.delete(book);
            }

            // Kategoriyi siliyoruz
            categoryRepository.delete(category);

            return ResponseEntity.ok().body("Category and associated books deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found!");
        }
    }
}

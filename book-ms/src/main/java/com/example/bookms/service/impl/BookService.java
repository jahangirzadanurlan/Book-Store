package com.example.bookms.service.impl;

import com.example.bookms.model.dto.request.BookRequestDto;
import com.example.bookms.model.entity.Book;
import com.example.bookms.model.entity.Category;
import com.example.bookms.model.entity.OrderedBooks;
import com.example.bookms.repository.BookRepository;
import com.example.bookms.repository.CategoryRepository;
import com.example.bookms.repository.OrderedBooksRepository;
import com.example.bookms.service.IBookService;
import com.example.commonsecurity.auth.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService implements IBookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final OrderedBooksRepository orderedBooksRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public ResponseEntity<String> saveBook(BookRequestDto request) {
        Optional<Category> categoryByName = categoryRepository.findCategoryByName(request.getCategoryName());
        if (categoryByName.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found!");
        }

        Book book = Book.builder()
                .name(request.getName())
                .author(request.getAuthor())
                .publishDate(request.getPublishDate())
                .pageCount(request.getPageCount())
                .price(request.getPrice())
                .categoryName(request.getCategoryName())
                .build();
        List<Book> categoryBooks = categoryByName.get().getBooks();
        categoryBooks.add(book);
        categoryByName.get().setBooks(categoryBooks);

        bookRepository.save(book);
        categoryRepository.save(categoryByName.get());

        System.out.println(book + "571");
        System.out.println(categoryByName + "571");


        return ResponseEntity.ok().body("Save is successfully!");
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteBook(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            Optional<Category> category = categoryRepository.findCategoryByName(book.getCategoryName());

            if (category.isPresent()) {
                // Category'deki books listesinden book'u kaldırma
                List<Book> books = category.get().getBooks();
                books.removeIf(b -> b.getId().equals(id));
                categoryRepository.save(category.get()); // Kategoriyi güncelle
            }

            // Book tablosundan silme işlemi
            bookRepository.delete(book);

            return ResponseEntity.ok().body("Book deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book not found!");
        }
    }

    @Override
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok().body(bookRepository.findAll());
    }

    @Override
    public ResponseEntity<Book> getBookById(Long id) {
        Optional<Book> bookById = bookRepository.findById(id);
        return bookById.map(book -> ResponseEntity.ok().body(book)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @Override
    public ResponseEntity<List<Book>> getBooksByCategoryName(String name) {
        Optional<Category> categoryByName = categoryRepository.findCategoryByName(name);
        return categoryByName.map(category -> ResponseEntity.ok().body(category.getBooks())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @Override
    public ResponseEntity<List<Book>> getBooksByName(String name) {
        return ResponseEntity.ok().body(bookRepository.findByNameContaining(name));
    }

    @Override
    @Transactional
    public ResponseEntity<String> buyBook(Long id, String token) {
        Optional<Book> bookById = bookRepository.findById(id);
        if (bookById.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found!");
        }

        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);

        Optional<OrderedBooks> orderedBooksByUsername = orderedBooksRepository.findOrderedBooksByUsername(username);
        if (orderedBooksByUsername.isPresent()){
            List<Book> books = orderedBooksByUsername.get().getBooks();
            books.add(bookById.get());
            orderedBooksByUsername.get().setBooks(books);
        }else {
            List<Book> books = new ArrayList<>();
            books.add(bookById.get());

            OrderedBooks orderedBooks = OrderedBooks.builder()
                    .username(username)
                    .books(books)
                    .build();
            orderedBooksRepository.save(orderedBooks);
        }

        return ResponseEntity.ok().body("Book purchased successfully");
    }

    @Override
    public ResponseEntity<List<Book>> getOrders(String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);

        Optional<OrderedBooks> orderedBooksByUsername = orderedBooksRepository.findOrderedBooksByUsername(username);

        return orderedBooksByUsername.map(orderedBooks -> ResponseEntity.ok().body(orderedBooks.getBooks())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


}

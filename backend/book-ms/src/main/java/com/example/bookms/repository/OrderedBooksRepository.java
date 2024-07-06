package com.example.bookms.repository;

import com.example.bookms.model.entity.OrderedBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderedBooksRepository extends JpaRepository<OrderedBooks,Long> {
    Optional<OrderedBooks> findOrderedBooksByUsername(String username);
}

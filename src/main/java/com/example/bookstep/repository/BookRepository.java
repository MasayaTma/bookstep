package com.example.bookstep.repository;

import com.example.bookstep.entity.Book;
import com.example.bookstep.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByOrderByCreatedAtDesc();

    List<Book> findTop5ByOrderByCreatedAtDesc();

    long countByStatus(BookStatus status);
}

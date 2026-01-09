package com.hemanth.librarymanagment.repository;

import com.hemanth.librarymanagment.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookEntityRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByIsbn(String isbn);
    Optional<BookEntity> findByIsbn(String isbn);
    List<BookEntity> findByPublishDate(LocalDateTime date);
    List<BookEntity> findByAuthorIdsAuthorName(String authorName);
    List<BookEntity> findByIsbnContainingIgnoreCase(String isbn); // optional
    List<BookEntity> findBookByBookTitle(String bookTitle);
}
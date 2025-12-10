package com.hemanth.librarymanagment.Interfaces;

import com.hemanth.librarymanagment.dto.BookDto;
import com.hemanth.librarymanagment.entity.BookEntity;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

public interface BookInterface {
    BookDto createBook(BookDto bookDto);
    BookDto getBookById(Long id);
    List<BookDto> getAllBooks();
    void deleteBookById(Long id);
    List<BookDto> getBooksByDate(LocalDate date);
    List<BookDto> findBookByTitle(String title);
    List<BookDto> findAllBookByAuthor(String author);
    BookDto updateBook(Long id, BookDto bookDto);
}

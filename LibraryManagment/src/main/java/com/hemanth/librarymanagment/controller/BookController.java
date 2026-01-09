package com.hemanth.librarymanagment.controller;

import com.hemanth.librarymanagment.dto.BookDto;
import com.hemanth.librarymanagment.dto.BookPatchDto;
import com.hemanth.librarymanagment.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id,
                                              @RequestBody @Valid BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<BookDto> updatePartialBook(@PathVariable Long id, @RequestBody @Valid BookPatchDto bookPatchDto){
        Map<String,Object> map=new HashMap<>();
        if(bookPatchDto.getBookTitle() != null) {
            map.put("title", bookPatchDto.getBookTitle());
        }
        if (bookPatchDto.getPrice() != null) {
            map.put("price", bookPatchDto.getPrice());
        }
        if (bookPatchDto.getAuthorIds() != null) {
            map.put("authorIds", bookPatchDto.getAuthorIds());
        }

        return ResponseEntity.ok(bookService.updatePartialBook(id,map));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

}

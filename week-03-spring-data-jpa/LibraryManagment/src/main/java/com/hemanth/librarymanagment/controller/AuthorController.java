package com.hemanth.librarymanagment.controller;

import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.service.AuthorService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/author")
@Validated
public class AuthorController {
    private final AuthorService authorService;
    public AuthorController(AuthorService authorService){
        this.authorService=authorService;
    }
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody @Valid AuthorDto authorDto){
        AuthorDto authorDto1=authorService.createAuthor(authorDto);
        return new ResponseEntity<>(authorDto1, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors(){
        return new ResponseEntity<>(authorService.getAllAuthor(),HttpStatus.OK);
    }
    @GetMapping("/{with-books}")
    public ResponseEntity<List<AuthorDto>> getAllAuthorsWithBooks(){
        return ResponseEntity.ok(authorService.getAllAuthorsWithBooks());
    }

    @GetMapping("/{author_id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable(name="author_id") Long id){
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthorById(@PathVariable Long id,
                                                      @RequestBody @Valid AuthorDto authorDto) {
        AuthorDto updated = authorService.updateAuthorById(id, authorDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable Long id) {
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<AuthorDto> findAuthorByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(authorService.findAuthorByName(name));
    }
    @GetMapping("/with-books")
    public ResponseEntity<List<AuthorDto>> getAuthorsWithBooks() {
        return ResponseEntity.ok(authorService.getAllAuthorsWithBooks());
    }


}

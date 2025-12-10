package com.hemanth.librarymanagment.Interfaces;

import com.hemanth.librarymanagment.dto.AuthorDto;

import java.util.List;

public interface AuthorInterface {
    AuthorDto createAuthor(AuthorDto authorDto);
    List<AuthorDto> getAllAuthor();
    AuthorDto getAuthorById(Long id);
    AuthorDto updateAuthorById(Long id,AuthorDto authorDto);
    void deleteAuthorById(Long id);
    AuthorDto findAuthorByName(String name);
}

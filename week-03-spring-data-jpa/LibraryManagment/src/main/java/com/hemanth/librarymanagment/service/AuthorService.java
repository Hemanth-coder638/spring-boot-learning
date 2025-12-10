package com.hemanth.librarymanagment.service;

import com.hemanth.librarymanagment.Interfaces.AuthorInterface;
import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.dto.BookDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.entity.BookEntity;
import com.hemanth.librarymanagment.exception.ResourceAlreadyExistException;
import com.hemanth.librarymanagment.exception.ResourceNotFoundException;
import com.hemanth.librarymanagment.repository.AuthorEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService implements AuthorInterface {
    private final AuthorEntityRepository authorRepository;
    private final ModelMapper modelMapper;
    public AuthorService(AuthorEntityRepository authorRepository,ModelMapper modelMapper ){
        this.authorRepository=authorRepository;
        this.modelMapper=modelMapper;
    }

    @Override
    @Transactional
    public AuthorDto createAuthor(AuthorDto authorDto) {
        if(authorRepository.existsByAuthorName(authorDto.getAuthorName()))
        {
            throw new ResourceAlreadyExistException("Duplicate names not allowed "+authorDto.getAuthorName());
        }

        else{
            AuthorEntity authorEntity = modelMapper.map(authorDto, AuthorEntity.class);
            AuthorEntity savedAuthor = authorRepository.saveAndFlush(authorEntity);
            AuthorDto authorDto1 = modelMapper.map(savedAuthor, AuthorDto.class);
            authorDto1.setCreatedAt(savedAuthor.getCreatedAt());
            authorDto1.setUpdatedAt(savedAuthor.getUpdatedAt());
            return authorDto1;
          }
    }

    @Override
    public List<AuthorDto> getAllAuthor() {
        List<AuthorEntity> entityList=authorRepository.findAll();
        List<AuthorDto> dtoList=entityList.stream().map(entity->modelMapper.map(entity,AuthorDto.class)).toList();
        return dtoList;
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        AuthorEntity entity = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found id= " + id));
        return modelMapper.map(entity, AuthorDto.class);
    }

    @Transactional
    public List<AuthorDto> getAllAuthorsWithBooks() {

        List<AuthorEntity> authors = authorRepository.findAllWithBooks();

        return authors.stream().map(author -> {

            AuthorDto dto = new AuthorDto();
            dto.setAuthorId(author.getAuthorId());
            dto.setAuthorName(author.getAuthorName());
            dto.setBiography(author.getBiography());
            dto.setCreatedAt(author.getCreatedAt());
            dto.setUpdatedAt(author.getUpdatedAt());

            // Map books
            Set<BookDto> books = author.getBookEntities()
                    .stream()
                    .map(book -> {
                        BookDto b = new BookDto();
                        b.setBookId(book.getBookId());
                        b.setBookTitle(book.getBookTitle());
                        b.setIsbn(book.getIsbn());
                        b.setPublishDate(book.getPublishDate());
                        b.setPrice(book.getPrice());
                        b.setCreatedAt(book.getCreatedAt());
                        b.setUpdateAt(book.getUpdateAt());

                        // map authorIds
                        Set<Long> authorIds = book.getAuthorEntities()
                                .stream()
                                .map(AuthorEntity::getAuthorId)
                                .collect(Collectors.toSet());

                        b.setAuthorIds(authorIds);
                        return b;
                    }).collect(Collectors.toSet());

            dto.setBookDto(books);
            return dto;
        }).toList();
    }


    @Override
    @Transactional
    public AuthorDto updateAuthorById(Long id,AuthorDto authorDto) {
        AuthorEntity entity = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found id = " + id));

        entity.setAuthorName(authorDto.getAuthorName());
        entity.setBiography(authorDto.getBiography());
        return modelMapper.map(entity, AuthorDto.class);
    }

    @Override
    @Transactional
    public void deleteAuthorById(Long id) {
        AuthorEntity entity = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found id = " + id));

        // Break relationship before delete
        entity.getBookEntities().forEach(book ->
                book.getAuthorEntities().remove(entity));

        authorRepository.delete(entity);
    }

    @Override
    public AuthorDto findAuthorByName(String name) {
        AuthorEntity entity = authorRepository.findByAuthorName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found name = " + name));
        return modelMapper.map(entity, AuthorDto.class);
    }
}

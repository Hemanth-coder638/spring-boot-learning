package com.hemanth.librarymanagment.service;

import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.exception.ResourceAlreadyExistException;
import com.hemanth.librarymanagment.exception.ResourceNotFoundException;
import com.hemanth.librarymanagment.repository.AuthorEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthorServiceTest {

    @Mock
    private AuthorEntityRepository authorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthorService authorService;

    private AuthorDto authorDto;
    private AuthorEntity authorEntity;

    // --------------------------------------------------
    // Test lifecycle
    // --------------------------------------------------

    @BeforeEach
    void setUp() {
        log.info("Setting up AuthorService test data");

        authorDto = new AuthorDto();
        authorDto.setAuthorName("Dr Mahesh");
        authorDto.setBiography("Spring Expert");

        authorEntity = new AuthorEntity();
        authorEntity.setAuthorId(1L);
        authorEntity.setAuthorName("Dr Mahesh");
        authorEntity.setBiography("Spring Expert");

        log.info("Test data initialized");
    }

    // --------------------------------------------------
    // createAuthor()
    // --------------------------------------------------

    @Test
    void createAuthor_shouldCreateAuthor_whenNameIsUnique() {
        log.info("Running createAuthor success scenario");

        when(authorRepository.existsByAuthorName("Dr Mahesh"))
                .thenReturn(false);

        when(modelMapper.map(authorDto, AuthorEntity.class))
                .thenReturn(authorEntity);

        when(authorRepository.saveAndFlush(authorEntity))
                .thenReturn(authorEntity);

        when(modelMapper.map(authorEntity, AuthorDto.class))
                .thenReturn(authorDto);

        AuthorDto result = authorService.createAuthor(authorDto);

        assertNotNull(result);
        assertEquals("Dr Mahesh", result.getAuthorName());

        verify(authorRepository).saveAndFlush(authorEntity);

        log.info("✔ Author created successfully");
    }

    @Test
    void createAuthor_shouldThrowException_whenDuplicateNameExists() {
        log.info("Running createAuthor duplicate scenario");

        when(authorRepository.existsByAuthorName("Dr Mahesh"))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
                () -> authorService.createAuthor(authorDto));

        verify(authorRepository, never()).saveAndFlush(any());

        log.info("✔ Duplicate author prevented");
    }

    // --------------------------------------------------
    // getAllAuthor()
    // --------------------------------------------------

    @Test
    void getAllAuthor_shouldReturnListOfAuthors() {
        log.info("Running getAllAuthor test");

        when(authorRepository.findAll())
                .thenReturn(List.of(authorEntity));

        when(modelMapper.map(authorEntity, AuthorDto.class))
                .thenReturn(authorDto);

        List<AuthorDto> result = authorService.getAllAuthor();

        assertEquals(1, result.size());
        assertEquals("Dr Mahesh", result.get(0).getAuthorName());

        log.info("✔ Authors fetched successfully");
    }

    // --------------------------------------------------
    // getAuthorById()
    // --------------------------------------------------

    @Test
    void getAuthorById_shouldReturnAuthor_whenIdExists() {
        log.info("Running getAuthorById success test");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(authorEntity));

        when(modelMapper.map(authorEntity, AuthorDto.class))
                .thenReturn(authorDto);

        AuthorDto result = authorService.getAuthorById(1L);

        assertEquals("Dr Mahesh", result.getAuthorName());

        log.info("✔ Author fetched by ID");
    }

    @Test
    void getAuthorById_shouldThrowException_whenIdNotFound() {
        log.info("Running getAuthorById not-found test");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authorService.getAuthorById(1L));

        log.info("Not-found exception thrown correctly");
    }

    // --------------------------------------------------
    // updateAuthorById()
    // --------------------------------------------------

    @Test
    void updateAuthorById_shouldUpdateAuthorSuccessfully() {
        log.info("Running updateAuthorById test");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(authorEntity));

        when(modelMapper.map(authorEntity, AuthorDto.class))
                .thenReturn(authorDto);

        AuthorDto result =
                authorService.updateAuthorById(1L, authorDto);

        assertEquals("Dr Mahesh", result.getAuthorName());

        log.info("Author updated successfully");
    }

    // --------------------------------------------------
    // deleteAuthorById()
    // --------------------------------------------------

    @Test
    void deleteAuthorById_shouldDeleteAuthor_whenExists() {
        log.info("Running deleteAuthorById test");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(authorEntity));

        authorService.deleteAuthorById(1L);

        verify(authorRepository).delete(authorEntity);

        log.info("Author deleted successfully");
    }

    // --------------------------------------------------
    // findAuthorByName()
    // --------------------------------------------------

    @Test
    void findAuthorByName_shouldReturnAuthor_whenExists() {
        log.info("Running findAuthorByName test");

        when(authorRepository.findByAuthorName("Dr Mahesh"))
                .thenReturn(Optional.of(authorEntity));

        when(modelMapper.map(authorEntity, AuthorDto.class))
                .thenReturn(authorDto);

        AuthorDto result =
                authorService.findAuthorByName("Dr Mahesh");

        assertEquals("Dr Mahesh", result.getAuthorName());

        log.info("Author fetched by name");
    }
}

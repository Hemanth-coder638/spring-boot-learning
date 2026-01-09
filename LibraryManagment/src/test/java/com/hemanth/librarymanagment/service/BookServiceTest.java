package com.hemanth.librarymanagment.service;

import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.dto.BookDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.entity.BookEntity;
import com.hemanth.librarymanagment.exception.ResourceAlreadyExistException;
import com.hemanth.librarymanagment.exception.ResourceNotFoundException;
import com.hemanth.librarymanagment.repository.AuthorEntityRepository;
import com.hemanth.librarymanagment.repository.BookEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class BookServiceTest {


    @Mock
    private BookEntityRepository bookRepository;

    @Mock
    private AuthorEntityRepository authorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookService bookService;

    private BookDto bookDto;
    private BookEntity bookEntity;
    private AuthorEntity authorEntity;

    // Test lifecycle

    @BeforeEach
    void setUp() {
        log.info("Setting up BookService test data");

        authorEntity = new AuthorEntity();
        authorEntity.setAuthorId(1L);
        authorEntity.setAuthorName("Dr Mahesh");

        bookEntity = new BookEntity();
        bookEntity.setBookId(10L);
        bookEntity.setBookTitle("Spring Boot Deep Dive");
        bookEntity.setIsbn("ISBN-123");
        bookEntity.setPrice(500);
        bookEntity.setPublishDate(LocalDateTime.now());
        bookEntity.setAuthorIds(new HashSet<>());

        bookDto = new BookDto();
        bookDto.setBookTitle("Spring Boot Deep Dive");
        bookDto.setIsbn("ISBN-123");
        bookDto.setPrice(500);
        bookDto.setPublishDate(LocalDateTime.now());
        bookDto.setAuthorIds(Set.of(1L));

        log.info("✔ Test data initialized");
    }

    // --------------------------------------------------
    // createBook()
    // --------------------------------------------------

    @Test
    void createBook_shouldCreateBook_whenIsbnIsUnique() {
        log.info("Running createBook success scenario");

        when(bookRepository.existsByIsbn("ISBN-123"))
                .thenReturn(false);

        when(modelMapper.map(bookDto, BookEntity.class))
                .thenReturn(bookEntity);

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(authorEntity));

        when(bookRepository.saveAndFlush(bookEntity))
                .thenReturn(bookEntity);

        when(modelMapper.map(bookEntity, BookDto.class))
                .thenReturn(bookDto);

        BookDto result = bookService.createBook(bookDto);

        assertNotNull(result);
        assertEquals("ISBN-123", result.getIsbn());

        verify(bookRepository).saveAndFlush(bookEntity);

        log.info("✔ Book created successfully");
    }

    @Test
    void createBook_shouldThrowException_whenDuplicateIsbnExists() {
        log.info("Running createBook duplicate ISBN scenario");

        when(bookRepository.existsByIsbn("ISBN-123"))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
                () -> bookService.createBook(bookDto));

        verify(bookRepository, never()).saveAndFlush(any());

        log.info("✔ Duplicate ISBN prevented");
    }

    // --------------------------------------------------
    // getBookById()
    // --------------------------------------------------

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        log.info("Running getBookById success test");

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(bookEntity));

        when(modelMapper.map(bookEntity, BookDto.class))
                .thenReturn(bookDto);

        BookDto result = bookService.getBookById(10L);

        assertEquals("ISBN-123", result.getIsbn());

        log.info("Book fetched by ID");
    }

    @Test
    void getBookById_shouldThrowException_whenNotFound() {
        log.info("Running getBookById not-found test");

        when(bookRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.getBookById(10L));

        log.info("Book not-found exception thrown");
    }

    // --------------------------------------------------
    // getAllBooks()
    // --------------------------------------------------

    @Test
    void getAllBooks_shouldReturnBooksWithAuthorIds() {
        log.info("Running getAllBooks test");

        bookEntity.getAuthorIds().add(authorEntity);

        when(bookRepository.findAll())
                .thenReturn(List.of(bookEntity));

        List<BookDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAuthorIds().contains(1L));

        log.info("✔ Books fetched successfully");
    }

    // --------------------------------------------------
    // deleteBookById()
    // --------------------------------------------------

    @Test
    void deleteBookById_shouldDeleteBook_whenExists() {
        log.info("Running deleteBookById test");

        bookEntity.getAuthorIds().add(authorEntity);

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(bookEntity));

        bookService.deleteBookById(10L);

        verify(bookRepository).delete(bookEntity);

        log.info("✔ Book deleted successfully");
    }

    // --------------------------------------------------
    // findBookByTitle()
    // --------------------------------------------------

    @Test
    void findBookByTitle_shouldReturnBooks_whenFound() {
        log.info("Running findBookByTitle test");

        when(bookRepository.findBookByBookTitle("Spring Boot Deep Dive"))
                .thenReturn(List.of(bookEntity));

        when(modelMapper.map(bookEntity, BookDto.class))
                .thenReturn(bookDto);

        List<BookDto> result =
                bookService.findBookByTitle("Spring Boot Deep Dive");

        assertFalse(result.isEmpty());

        log.info("✔ Book found by title");
    }

    @Test
    void findBookByTitle_shouldThrowException_whenNotFound() {
        log.info("Running findBookByTitle not-found test");

        when(bookRepository.findBookByBookTitle("Unknown"))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.findBookByTitle("Unknown"));

        log.info("✔ Title not-found exception thrown");
    }

    // --------------------------------------------------
    // findAllBookByAuthor()
    // --------------------------------------------------

    @Test
    void findAllBookByAuthor_shouldReturnBooks() {
        log.info("Running findAllBookByAuthor test");

        when(bookRepository.findByAuthorIdsAuthorName("Dr Mahesh"))
                .thenReturn(List.of(bookEntity));

        when(modelMapper.map(bookEntity, BookDto.class))
                .thenReturn(bookDto);

        List<BookDto> result =
                bookService.findAllBookByAuthor("Dr Mahesh");

        assertEquals(1, result.size());

        log.info("✔ Books fetched by author");
    }

    // --------------------------------------------------
    // isExistById()
    // --------------------------------------------------

    @Test
    void isExistById_shouldReturnTrue_whenExists() {
        log.info("Running isExistById success test");

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(bookEntity));

        assertTrue(bookService.isExistById(10L));

        log.info("✔ Book existence verified");
    }

    @Test
    void isExistById_shouldThrowException_whenNotFound() {
        log.info("Running isExistById not-found test");

        when(bookRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.isExistById(10L));

        log.info("✔ Book not-found exception thrown");
    }
}

package com.hemanth.librarymanagment.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hemanth.librarymanagment.config.PostgresContainer;
import com.hemanth.librarymanagment.config.TestContainerConfiguration;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.entity.BookEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.*;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

@Testcontainers
@Slf4j
@ActiveProfiles("test")
@DataJpaTest
@Import(TestContainerConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookEntityRepositoryTest extends PostgresContainer {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withCommand("postgres", "-c", "timezone=UTC"); // Fix timezone

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
    }

    @Autowired
    private BookEntityRepository bookRepository;

    @Autowired
    private AuthorEntityRepository authorRepository;

    private BookEntity savedBook;
    private AuthorEntity savedAuthor;

    // ----------------------------------------------------------------
    // Test lifecycle
    // ----------------------------------------------------------------

    @BeforeEach
    void setUp() {
        log.info("Test setup started");

        // Create author
        AuthorEntity author = new AuthorEntity();
        author.setAuthorName("Dr Mahesh");
        author.setBiography("Spring expert");

        savedAuthor = authorRepository.save(author);

        // Create book
        BookEntity book = new BookEntity();
        book.setBookTitle("Spring Boot Deep Dive");
        book.setIsbn("ISBN-123");
        book.setPrice(500);
        book.setPublishDate(LocalDateTime.of(2024, 1, 10, 10, 0));

        // Maintain bidirectional relationship
        book.getAuthorIds().add(savedAuthor);
        savedAuthor.getBookEntities().add(book);

        savedBook = bookRepository.save(book);

        log.info("Test data prepared. Book ID={}, ISBN={}",
                savedBook.getBookId(),
                savedBook.getIsbn());
    }

    @AfterEach
    void tearDown() {
        log.info("Cleaning database after test");
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        log.info("Database cleaned");
    }

    // ----------------------------------------------------------------
    // Test cases
    // ----------------------------------------------------------------

    @Test
    void existsByIsbn_shouldReturnTrue_whenBookExists() {
        log.info("Running existsByIsbn test");

        boolean exists = bookRepository.existsByIsbn("ISBN-123");

        log.info("Book exists result: {}", exists);

        assertTrue(exists);
    }

    @Test
    void findByIsbn_shouldReturnBook_whenBookExists() {
        log.info("Running findByIsbn (exists) test");

        Optional<BookEntity> result =
                bookRepository.findByIsbn("ISBN-123");

        assertTrue(result.isPresent());
        assertEquals("Spring Boot Deep Dive",
                result.get().getBookTitle());

        log.info("Book found with ID={}", result.get().getBookId());
    }

    @Test
    void findByIsbn_shouldReturnEmpty_whenBookDoesNotExist() {
        log.info("Running findByIsbn (not exists) test");

        Optional<BookEntity> result =
                bookRepository.findByIsbn("UNKNOWN-ISBN");

        assertFalse(result.isPresent());

        log.info("Book not found as expected");
    }

    @Test
    void findByPublishDate_shouldReturnBooks_whenDateMatches() {
        log.info("Running findByPublishDate test");

        List<BookEntity> books =
                bookRepository.findByPublishDate(
                        LocalDateTime.of(2024, 1, 10, 10, 0));

        assertFalse(books.isEmpty());
        assertEquals("ISBN-123", books.get(0).getIsbn());

        log.info("Books found by publish date: {}", books.size());
    }

    @Test
    void findByAuthorIdsAuthorName_shouldReturnBooks_whenAuthorExists() {
        log.info("Running findByAuthorIdsAuthorName test");

        List<BookEntity> books =
                bookRepository.findByAuthorIdsAuthorName("Dr Mahesh");

        assertFalse(books.isEmpty());
        assertEquals("Spring Boot Deep Dive",
                books.get(0).getBookTitle());

        log.info("Books found for author Dr Mahesh: {}", books.size());
    }

    @Test
    void findByIsbnContainingIgnoreCase_shouldReturnMatchingBooks() {
        log.info("Running findByIsbnContainingIgnoreCase test");

        List<BookEntity> books =
                bookRepository.findByIsbnContainingIgnoreCase("isbn");

        assertFalse(books.isEmpty());

        log.info("Partial ISBN match count: {}", books.size());
    }

    @Test
    void findBookByBookTitle_shouldReturnBooks_whenTitleMatches() {
        log.info("Running findBookByBookTitle test");

        List<BookEntity> books =
                bookRepository.findBookByBookTitle("Spring Boot Deep Dive");

        assertFalse(books.isEmpty());
        assertEquals("ISBN-123", books.get(0).getIsbn());

        log.info("Books found by title: {}", books.size());
    }
}

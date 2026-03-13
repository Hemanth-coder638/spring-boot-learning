package com.hemanth.librarymanagment.repository;

import com.hemanth.librarymanagment.config.PostgresContainer;
import com.hemanth.librarymanagment.config.TestContainerConfiguration;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import com.hemanth.librarymanagment.entity.BookEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Slf4j
@Import(TestContainerConfiguration.class)
class AuthorEntityRepositoryTest  extends PostgresContainer {
    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // OVERRIDE timezone to UTC explicitly
        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone", () -> "UTC");
        registry.add("spring.datasource.hikari.connection-init-sql",
                () -> "SET TIME ZONE 'UTC'");
    }

    @Autowired
    private AuthorEntityRepository authorRepository;

    private AuthorEntity savedAuthor;

    // Test Lifecycle

     @BeforeEach
    void setUp() {
        log.info("Test setup started");

        AuthorEntity author = new AuthorEntity();
        author.setAuthorName("Dr Mahesh");
        author.setBiography("Test biography");

        savedAuthor = authorRepository.save(author);

        log.info("Author saved in setup. ID={}, Name={}",
                savedAuthor.getAuthorId(),
                savedAuthor.getAuthorName());
    }

    @AfterEach
    void tearDown() {
        log.info("Cleaning database after test");
        authorRepository.deleteAll();
        log.info("Database cleaned");
    }

    // Test cases

    @Test
    void existsByAuthorName_shouldReturnTrue_whenAuthorExists() {
        log.info("Running existsByAuthorName test");

        boolean exists =
                authorRepository.existsByAuthorName("Dr Mahesh");

        log.info("Author exists result: {}", exists);

        assertTrue(exists);
    }

    @Test
    void findByAuthorName_shouldReturnAuthor_whenAuthorExists() {
        log.info("Running findByAuthorName (exists) test");

        Optional<AuthorEntity> result =
                authorRepository.findByAuthorName("Dr Mahesh");

        assertTrue(result.isPresent());
        assertEquals("Dr Mahesh", result.get().getAuthorName());

        log.info("Author found with ID={}", result.get().getAuthorId());
    }

    @Test
    void findByAuthorName_shouldReturnEmpty_whenAuthorDoesNotExist() {
        log.info("Running findByAuthorName (not exists) test");

        Optional<AuthorEntity> result =
                authorRepository.findByAuthorName("Unknown");

        assertFalse(result.isPresent());

        log.info("Author not found as expected");
    }

    @Test
    void findAllWithBooks_shouldFetchAuthorsWithBooks() {
        log.info("Running findAllWithBooks test");

        BookEntity book = new BookEntity();
        book.setBookTitle("Spring Boot Deep Dive");
        book.setIsbn("ISBN-123");
        book.setPrice(500);
        book.setPublishDate(LocalDateTime.now());

        // Maintain bidirectional relationship
        savedAuthor.getBookEntities().add(book);
        book.getAuthorIds().add(savedAuthor);

        authorRepository.save(savedAuthor);

        log.info("Book associated with author. Book title={}",
                book.getBookTitle());

        List<AuthorEntity> authors =
                authorRepository.findAllWithBooks();

        assertFalse(authors.isEmpty());
        assertFalse(authors.get(0).getBookEntities().isEmpty());

        log.info("Authors fetched with books successfully");
    }
}

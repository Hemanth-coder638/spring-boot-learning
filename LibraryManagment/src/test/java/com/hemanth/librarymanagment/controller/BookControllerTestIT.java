package com.hemanth.librarymanagment.controller;

//package com.hemanth.librarymanagment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
        import com.hemanth.librarymanagment.dto.BookDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
        import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

        import java.util.Set;

        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class BookControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDto createBookDto() {
        BookDto dto = new BookDto();
        dto.setBookTitle("Spring Boot In Action");
        dto.setIsbn("ISBN-" + System.currentTimeMillis());
        dto.setPrice(499);
        dto.setAuthorIds(Set.of()); // ✅ important
        return dto;
    }

    @Test
    void createBook_shouldReturnCreatedBook() throws Exception {
        log.info("GIVEN: Valid BookDto");

        BookDto dto = createBookDto();

        log.info("WHEN: POST /book");

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value(dto.getIsbn()));

        log.info("THEN: Book created successfully");
    }

    @Test
    void getAllBooks_shouldReturnList() throws Exception {
        log.info("WHEN: GET /book");

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        log.info("THEN: Book list returned");
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        log.info("GIVEN: Book exists");

        BookDto dto = createBookDto();

        String response = mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto saved = objectMapper.readValue(response, BookDto.class);

        log.info("WHEN: GET /book/{}", saved.getBookId());

        mockMvc.perform(get("/book/{id}", saved.getBookId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(dto.getIsbn()));

        log.info("THEN: Book fetched successfully");
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        log.info("GIVEN: Existing Book");

        BookDto dto = createBookDto();

        String response = mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto saved = objectMapper.readValue(response, BookDto.class);

        saved.setBookTitle("Updated Title");

        log.info("WHEN: PUT /book/{}", saved.getBookId());

        mockMvc.perform(put("/book/{id}", saved.getBookId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Updated Title"));

        log.info("THEN: Book updated successfully");
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        log.info("GIVEN: Existing Book");

        BookDto dto = createBookDto();

        String response = mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookDto saved = objectMapper.readValue(response, BookDto.class);

        log.info("WHEN: DELETE /book/{}", saved.getBookId());

        mockMvc.perform(delete("/book/{id}", saved.getBookId()))
                .andExpect(status().isNoContent());

        log.info("THEN: Book deleted successfully");
    }
}

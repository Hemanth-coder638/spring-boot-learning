package com.hemanth.librarymanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hemanth.librarymanagment.config.TestContainerConfiguration;
import com.hemanth.librarymanagment.dto.AuthorDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfiguration.class)
@Transactional
@Slf4j
class AuthorControllerTestIT {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthorDto authorDto;
    // Setup

    @BeforeEach
    void setUp() {
        log.info("Setting up AuthorController integration test");

        authorDto = new AuthorDto();
        authorDto.setAuthorName("Dr Mahesh");
        authorDto.setBiography("Spring expert");

        log.info("Test data prepared");
    }
    // POST /author

    @Test
    void createAuthor_shouldReturn200_whenValidRequest() throws Exception {
        log.info("Running createAuthor API test");

        mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName")
                        .value("Dr Mahesh"));

        log.info("Author created successfully via API");
    }
    // GET /author

    @Test
    void getAllAuthors_shouldReturnList() throws Exception {
        log.info("Running getAllAuthors API test");

        // create first
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorDto)));

        mockMvc.perform(get("/author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        log.info("Authors fetched successfully");
    }

    // GET /author/{id}

    @Test
    void getAuthorById_shouldReturnAuthor() throws Exception {
        log.info("Running getAuthorById API test");

        String response = mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDto saved =
                objectMapper.readValue(response, AuthorDto.class);

        mockMvc.perform(get("/author/{id}", saved.getAuthorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName")
                        .value("Dr Mahesh"));

        log.info("Author fetched by ID");
    }
    // PUT /author/{id}
    @Test
    void updateAuthor_shouldUpdateAuthor() throws Exception {
        log.info("Running updateAuthor API test");

        String response = mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDto saved =
                objectMapper.readValue(response, AuthorDto.class);

        saved.setBiography("Updated biography");

        mockMvc.perform(put("/author/{id}", saved.getAuthorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biography")
                        .value("Updated biography"));

        log.info("Author updated successfully");
    }
    // DELETE /author/{id}

    @Test
    void deleteAuthor_shouldReturn204() throws Exception {
        log.info("Running deleteAuthor API test");

        String response = mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthorDto saved =
                objectMapper.readValue(response, AuthorDto.class);

        mockMvc.perform(delete("/author/{id}", saved.getAuthorId()))
                .andExpect(status().isNoContent());

        log.info("Author deleted successfully");
    }

    // GET /author/search?name=

    @Test
    void findAuthorByName_shouldReturnAuthor() throws Exception {
        log.info("Running findAuthorByName API test");

        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorDto)));

        mockMvc.perform(get("/author/search")
                        .param("name", "Dr Mahesh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName")
                        .value("Dr Mahesh"));

        log.info("Author found by name");
    }

    // GET /author/with-books

    @Test
    void getAuthorsWithBooks_shouldReturnEmptyBooksInitially() throws Exception {
        log.info("Running getAuthorsWithBooks API test");

        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorDto)));

        mockMvc.perform(get("/author/with-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        log.info("Authors with books fetched");
    }
}

package com.hemanth.spring_ai_lab.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

@Component
public class UserManualPdfIngestionRunner implements CommandLineRunner {

    private final VectorStore routerVectorStore;
    private final JdbcTemplate jdbcTemplate;

    public UserManualPdfIngestionRunner(
            @Qualifier("routerVectorStore") VectorStore routerVectorStore,
            JdbcTemplate jdbcTemplate) {

        this.routerVectorStore = routerVectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM routerusermanualvc",
                Integer.class
        );

        if (count != null && count > 0) {
            System.out.println("User manual already ingested. Skipping ingestion.");
            return;
        }

        System.out.println("Ingesting User Manual PDF...");

        PagePdfDocumentReader reader =
                new PagePdfDocumentReader(new ClassPathResource("user_manual.pdf"));

        List<Document> documents = reader.get();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        routerVectorStore.add(chunks);
        System.out.println("User manual loaded into vector database.");
    }
}
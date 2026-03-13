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
public class PolicyPdfIngestionRunner implements CommandLineRunner {

    private final VectorStore policyVectorStore;
    private final JdbcTemplate jdbcTemplate;

    public PolicyPdfIngestionRunner(
            @Qualifier("policyVectorStore") VectorStore policyVectorStore,
            JdbcTemplate jdbcTemplate) {

        this.policyVectorStore = policyVectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM policy_vector_store",
                Integer.class
        );

        if (count != null && count > 0) {
            System.out.println("Policy PDF already ingested. Skipping ingestion.");
            return;
        }

        System.out.println("Ingesting Policy PDF...");

        PagePdfDocumentReader reader =
                new PagePdfDocumentReader(new ClassPathResource("policy.pdf"));

        List<Document> documents = reader.get();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocs = splitter.apply(documents);

        policyVectorStore.add(splitDocs);

        System.out.println("Policy PDF successfully loaded into Vector DB");
    }
}
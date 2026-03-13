package com.hemanth.spring_ai_lab.service;


import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final VectorStore songvectorStore;

   public SongService(@Qualifier("songVectorStore") VectorStore songVectorStore){
          this.songvectorStore=songVectorStore;
    }

    @PostConstruct
    public void loadSongs() {

        List<Document> songs = List.of(

                new Document("Fix You by Coldplay. A song about comforting someone during sadness.",
                        java.util.Map.of("genre", "Rock", "title", "Fix You")),

                new Document("Bohemian Rhapsody by Queen. Emotional and dramatic journey.",
                        java.util.Map.of("genre", "Rock", "title", "Bohemian Rhapsody")),

                new Document("Shape of You by Ed Sheeran. Romantic upbeat love story.",
                        java.util.Map.of("genre", "Pop", "title", "Shape of You")),

                new Document("Numb by Linkin Park. Feeling pressure and emotional exhaustion.",
                        java.util.Map.of("genre", "Rock", "title", "Numb")),

                new Document("Someone Like You by Adele. Heartbreak and longing.",
                        java.util.Map.of("genre", "Pop", "title", "Someone Like You"))

        );
        songvectorStore.add(songs);
    }
    public List<Document> searchVibe(String feeling) {

        return songvectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(feeling)
                        .topK(3)
                        .build()
        );
    }
}

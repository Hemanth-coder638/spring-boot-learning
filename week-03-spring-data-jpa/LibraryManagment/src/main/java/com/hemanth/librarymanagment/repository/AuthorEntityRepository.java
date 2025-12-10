package com.hemanth.librarymanagment.repository;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface AuthorEntityRepository extends JpaRepository<AuthorEntity, Long> {
    boolean existsByAuthorName(String authorName);
    Optional<AuthorEntity> findByAuthorName(String authorName);

    @EntityGraph(attributePaths = "bookEntities")
    @Query("SELECT a FROM AuthorEntity a LEFT JOIN FETCH a.bookEntities")
    List<AuthorEntity> findAllWithBooks();
}
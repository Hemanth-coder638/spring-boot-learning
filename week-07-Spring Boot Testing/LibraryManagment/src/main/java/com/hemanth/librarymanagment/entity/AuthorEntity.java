package com.hemanth.librarymanagment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"authorName"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long authorId;
    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private String biography;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @JsonIgnoreProperties("bookEntities")
    @ManyToMany(mappedBy = "authorIds",fetch = FetchType.LAZY)
    private Set<BookEntity> bookEntities=new HashSet<>();
}

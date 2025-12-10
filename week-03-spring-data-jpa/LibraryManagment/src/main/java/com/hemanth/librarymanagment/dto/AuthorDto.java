package com.hemanth.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hemanth.librarymanagment.entity.BookEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hemanth.librarymanagment.dto.BookDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AuthorDto {

    private Long authorId;
    @NotBlank(message = "Name is mandatory")
    private String authorName;
    @Size(max = 40,message = "biography cannot be more than 40 characters")
    private String biography;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private Set<BookDto> bookDto=new HashSet<>();

}

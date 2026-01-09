package com.hemanth.librarymanagment.dto;

import com.hemanth.librarymanagment.validation.AuthorIdsValidate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookPatchDto {
    private String bookTitle;

    private Integer price;

    @AuthorIdsValidate
    private Set<?> authorIds;
}

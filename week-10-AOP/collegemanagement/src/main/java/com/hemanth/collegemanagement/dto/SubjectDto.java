package com.hemanth.collegemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDto {
    private Long id;
    private String title;
    private Long professorId;
    private Set<Long> studentIds;
}

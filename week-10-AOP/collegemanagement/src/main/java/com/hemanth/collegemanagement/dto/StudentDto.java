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
public class StudentDto {
    private Long id;
    private String name;
    private Set<Long> professorIds; // avoid full nested objects
    private Set<Long> subjectIds;
    private Long admissionRecordId;
}

package com.hemanth.collegemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmissionRecordDto {
    private Long id;
    private Integer fees;
    private Long studentId;
}

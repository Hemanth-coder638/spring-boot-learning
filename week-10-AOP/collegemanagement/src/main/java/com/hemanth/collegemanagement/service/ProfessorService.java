package com.hemanth.collegemanagement.service;

import com.hemanth.collegemanagement.dto.ProfessorDto;

import java.util.List;

public interface ProfessorService {
    ProfessorDto createProfessor(ProfessorDto dto,String token);
    ProfessorDto getProfessor(Long id);
    List<ProfessorDto> getAllProfessors(String token);
    ProfessorDto updateProfessor(Long id, ProfessorDto dto);
    void deleteProfessor(Long id);
}

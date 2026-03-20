package com.hemanth.collegemanagement.service;

import com.hemanth.collegemanagement.aop.annotation.TrackExecutionTime;
import com.hemanth.collegemanagement.aop.annotation.ValidateDto;
import com.hemanth.collegemanagement.dto.ProfessorDto;
import com.hemanth.collegemanagement.entity.ProfessorEntity;
import com.hemanth.collegemanagement.repository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@TrackExecutionTime
@RequiredArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ModelMapper modelMapper;

    @Override
    @ValidateDto
    public ProfessorDto createProfessor(ProfessorDto dto,String token) {
        ProfessorEntity p = new ProfessorEntity();
       p.setTitle(dto.getTitle());
        ProfessorEntity saved = professorRepository.save(p);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDto getProfessor(Long id) {
        ProfessorEntity p = professorRepository.findWithRelationsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor not found " + id));
        return toDto(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessorDto> getAllProfessors(String token) {
        //simulating short delay for aop testing
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return professorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ProfessorDto updateProfessor(Long id, ProfessorDto dto) {
        ProfessorEntity p = professorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        p.setTitle(dto.getTitle());
        return toDto(professorRepository.save(p));
    }

    @Override
    public void deleteProfessor(Long id) {
        professorRepository.deleteById(id);
    }

    private ProfessorDto toDto(ProfessorEntity p) {
        ProfessorDto dto = new ProfessorDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setStudentIds(p.getStudentEntities().stream().map(x -> x.getId()).collect(Collectors.toSet()));
        dto.setSubjectIds(p.getSubjectEntities().stream().map(x -> x.getId()).collect(Collectors.toSet()));
        return dto;
    }
}

package com.hemanth.collegemanagement.service;

import com.hemanth.collegemanagement.aop.annotation.TrackExecutionTime;
import com.hemanth.collegemanagement.aop.annotation.ValidateDto;
import com.hemanth.collegemanagement.dto.StudentDto;
import com.hemanth.collegemanagement.entity.ProfessorEntity;
import com.hemanth.collegemanagement.entity.StudentEntity;
import com.hemanth.collegemanagement.entity.SubjectEntity;
import com.hemanth.collegemanagement.repository.ProfessorRepository;
import com.hemanth.collegemanagement.repository.StudentRepository;
import com.hemanth.collegemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@TrackExecutionTime
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              ProfessorRepository professorRepository,
                              SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @ValidateDto
    public StudentDto createStudent(StudentDto dto,String token) {
        StudentEntity s = new StudentEntity();
        s.setName(dto.getName());
        StudentEntity saved = studentRepository.save(s);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudent(Long id) {
        // use repository method that fetches relations in one go (avoid N+1)
        StudentEntity s = studentRepository.findWithRelationsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found " + id));
        return toDto(s);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public List<StudentDto> getAllStudents(String token) {
        // naive fetch: if you need to avoid N+1 here, you'd use a repository method with join fetch or projection
        return studentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public StudentDto updateStudent(Long id, StudentDto dto) {
        StudentEntity s = studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        s.setName(dto.getName());
        StudentEntity saved = studentRepository.save(s);
        return toDto(saved);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public StudentDto addProfessorToStudent(Long studentId, Long professorId,String token) {
        StudentEntity s = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
        ProfessorEntity p = professorRepository.findById(professorId).orElseThrow(() -> new EntityNotFoundException("Professor not found"));
        s.addProfessor(p); // maintains both sides
        StudentEntity saved = studentRepository.save(s);
        return toDto(saved);
    }

    @Override
    public StudentDto addSubjectToStudent(Long studentId, Long subjectId,String token) {
        StudentEntity s = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
        SubjectEntity subj = subjectRepository.findById(subjectId).orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        s.addSubject(subj);
        StudentEntity saved = studentRepository.save(s);
        return toDto(saved);
    }

    private StudentDto toDto(StudentEntity s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setProfessorIds(s.getProfessorEntities().stream().map(ProfessorEntity::getId).collect(Collectors.toSet()));
        dto.setSubjectIds(s.getSubjectEntities().stream().map(SubjectEntity::getId).collect(Collectors.toSet()));
        dto.setAdmissionRecordId(s.getAdmissionRecord() != null ? s.getAdmissionRecord().getId() : null);
        return dto;
    }
}

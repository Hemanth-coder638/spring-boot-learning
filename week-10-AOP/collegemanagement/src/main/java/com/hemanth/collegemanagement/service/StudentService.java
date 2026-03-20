package com.hemanth.collegemanagement.service;

import com.hemanth.collegemanagement.dto.StudentDto;

import java.util.List;

public interface StudentService {
    StudentDto createStudent(StudentDto dto,String token);
    StudentDto getStudent(Long id);
    List<StudentDto> getAllStudents(String token);
    StudentDto updateStudent(Long id, StudentDto dto);
    void deleteStudent(Long id);

    // relationship operations
    StudentDto addProfessorToStudent(Long studentId, Long professorId,String token);
    StudentDto addSubjectToStudent(Long studentId, Long subjectId,String token);
}

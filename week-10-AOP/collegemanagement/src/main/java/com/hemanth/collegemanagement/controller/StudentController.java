package com.hemanth.collegemanagement.controller;

import com.hemanth.collegemanagement.dto.StudentDto;
import com.hemanth.collegemanagement.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentDto> create(@RequestBody StudentDto dto){
        return ResponseEntity.ok(studentService.createStudent(dto,"VALID_TOKEN"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> get(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> all(){
        return ResponseEntity.ok(studentService.getAllStudents("VALID_TOKEN"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> update(@PathVariable Long id, @RequestBody StudentDto dto){
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studentId}/professors/{professorId}")
    public ResponseEntity<StudentDto> addProfessor(@PathVariable Long studentId, @PathVariable Long professorId){
        return ResponseEntity.ok(studentService.addProfessorToStudent(studentId, professorId,"VALID_TOKEN"));
    }

    @PostMapping("/{studentId}/subjects/{subjectId}")
    public ResponseEntity<StudentDto> addSubject(@PathVariable Long studentId, @PathVariable Long subjectId){
        return ResponseEntity.ok(studentService.addSubjectToStudent(studentId, subjectId,"VALID_TOKEN"));
    }
}

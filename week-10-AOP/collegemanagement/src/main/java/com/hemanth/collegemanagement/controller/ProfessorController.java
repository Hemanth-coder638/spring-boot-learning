package com.hemanth.collegemanagement.controller;

import com.hemanth.collegemanagement.dto.ProfessorDto;
import com.hemanth.collegemanagement.service.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    private final ProfessorService professorService;
    public ProfessorController(ProfessorService professorService){
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorDto> create(@RequestBody ProfessorDto dto){
        return ResponseEntity.ok(professorService.createProfessor(dto,"VALID_TOKEN"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDto> get(@PathVariable Long id){
        return ResponseEntity.ok(professorService.getProfessor(id));
    }

    @GetMapping
    public ResponseEntity<List<ProfessorDto>> all(){
        return ResponseEntity.ok(professorService.getAllProfessors("VALID_TOKEN"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDto> update(@PathVariable Long id, @RequestBody ProfessorDto dto){
        return ResponseEntity.ok(professorService.updateProfessor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
    }
}

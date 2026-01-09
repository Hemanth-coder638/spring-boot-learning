package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.controllers;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.advices.ApiResponse;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.dto.DepartmentDTO;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.DepartmentEntity;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.exceptions.ResourceNotFoundException;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.repositories.DepartmentRepository;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path="/department")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService){
        this.departmentService=departmentService;
    }
    @GetMapping(path = "/{departmentId}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable(name="departmentId") Long id){
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }
    @PostMapping
    public ResponseEntity<DepartmentDTO> createNewDepartment(@RequestBody @Valid DepartmentDTO departmentDTO){
        DepartmentDTO departmentDTO1=departmentService.createNewDepartment(departmentDTO);
        return new ResponseEntity<>(departmentDTO1, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(@RequestParam(required = false) String sortyBy){
        List<DepartmentDTO> departmentDTOList=departmentService.getAllDepartments();
        return ResponseEntity.ok(departmentDTOList);
    }
    @PutMapping(path="/{departmentId}")
    public ResponseEntity<DepartmentDTO> UpdateById(@RequestBody @Valid DepartmentDTO departmentDTO,@PathVariable(name="departmentId") Long departmentid){
        return ResponseEntity.ok(departmentService.upadateById(departmentDTO,departmentid));
    }
    @PatchMapping(path="/{departmentId}")
    public ResponseEntity<DepartmentDTO> UpdatePartialById(@RequestBody @Valid Map<String,Object> stringObjectMap,@PathVariable(name="departmentId") Long id){
        DepartmentDTO departmentDTO=departmentService.upadatePartialById(stringObjectMap,id);
        if (departmentDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(departmentDTO);
    }
    @DeleteMapping(path="/{departmentId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable(name="departmentId") Long id){
        boolean gotDeleted= departmentService.deleteById(id);
        if(gotDeleted) return ResponseEntity.ok(true);
        return ResponseEntity.notFound().build();
    }

}

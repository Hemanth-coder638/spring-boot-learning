package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.services;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.dto.DepartmentDTO;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.DepartmentEntity;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.EmployeeEntity;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.exceptions.ResourceNotFoundException;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.repositories.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    public DepartmentService(DepartmentRepository departmentRepository,ModelMapper modelMapper){
        this.departmentRepository=departmentRepository;
        this.modelMapper=modelMapper;
    }
    public DepartmentDTO getDepartmentById(Long id){
        isDepartmentExistById(id);
        DepartmentEntity DepartmentEntity=departmentRepository.findById(id).get();
        return modelMapper.map(DepartmentEntity,DepartmentDTO.class);
    }
    public DepartmentDTO createNewDepartment(DepartmentDTO departmentDTO){
        DepartmentEntity departmentEntity=modelMapper.map(departmentDTO,DepartmentEntity.class);
        DepartmentEntity savedDepartmentEntity=departmentRepository.save(departmentEntity);
        return modelMapper.map(savedDepartmentEntity,DepartmentDTO.class);
    }
    public List<DepartmentDTO> getAllDepartments(){
        List<DepartmentEntity> departmentEntities= departmentRepository.findAll();
        return departmentEntities.stream().map(departmentEntity -> modelMapper.map(departmentEntity,DepartmentDTO.class)).collect(Collectors.toList());
    }
    public DepartmentDTO upadateById(DepartmentDTO departmentDTO,Long id){
        isDepartmentExistById(id);
        DepartmentEntity departmentEntity=modelMapper.map(departmentDTO,DepartmentEntity.class);
        DepartmentEntity savedDepartmentEntity=departmentRepository.save(departmentEntity);
        return modelMapper.map(savedDepartmentEntity,DepartmentDTO.class);
    }
    public DepartmentDTO upadatePartialById(Map<String,Object> stringObjectMap,Long id){
        isDepartmentExistById(id);
        DepartmentEntity departmentEntity=departmentRepository.getReferenceById(id);
        stringObjectMap.forEach((field,value)->{
            if(field.equals("Id")) throw new ResourceNotFoundException("Id cannot be updated");
            Field fieldtobeupdated= ReflectionUtils.findRequiredField(DepartmentEntity.class,field);
            fieldtobeupdated.setAccessible(true);
            ReflectionUtils.setField(fieldtobeupdated,departmentEntity,value);
        });
        return modelMapper.map(departmentRepository.save(departmentEntity),DepartmentDTO.class);

    }
    public boolean deleteById(Long id){
        isDepartmentExistById(id);
        departmentRepository.deleteById(id);
        return true;
    }
    public void isDepartmentExistById(Long id){
        boolean exist=departmentRepository.existsById(id);
        if(!exist) throw new ResourceNotFoundException("Department not found with id "+id);
    }
}

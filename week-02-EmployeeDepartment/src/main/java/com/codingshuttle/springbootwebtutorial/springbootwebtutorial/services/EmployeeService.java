package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.services;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.dto.EmployeeDTO;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.EmployeeEntity;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.exceptions.ResourceNotFoundException;
import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring Service component — eligible for auto-detection
public class EmployeeService {

    // Injecting Spring Data JPA Repository to interact with the database
    private final EmployeeRepository employeeRepository;

    // Injecting ModelMapper to convert Entity <--> DTO objects automatically
    private final ModelMapper modelMapper;

    // Constructor-based Dependency Injection
    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    // ===========================================================================================
    // 1️⃣ GET SINGLE EMPLOYEE BY ID
    // ===========================================================================================
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        // ✔ Fetch employee from database using repository
        // ✔ Convert EmployeeEntity → EmployeeDTO using ModelMapper if present
        return employeeRepository.findById(id)
                .map(employeeEntity -> modelMapper.map(employeeEntity, EmployeeDTO.class));
    }

    // ===========================================================================================
    // 2️⃣ GET ALL EMPLOYEES
    // ===========================================================================================
    public List<EmployeeDTO> getAllEmployees() {
        // ✔ Fetch all records from DB
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();

        // ✔ Convert List<EmployeeEntity> to List<EmployeeDTO> using Java Stream API + ModelMapper
        return employeeEntities.stream()
                .map(employeeEntity -> modelMapper.map(employeeEntity, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    // ===========================================================================================
    // 3️⃣ CREATE A NEW EMPLOYEE ENTRY
    // ===========================================================================================
    public EmployeeDTO createNewEmployee(EmployeeDTO inputEmployee) {
        // ✔ Convert DTO → Entity to save into Database
        EmployeeEntity toSaveEntity = modelMapper.map(inputEmployee, EmployeeEntity.class);

        // ✔ Save to DB using repository
        EmployeeEntity savedEmployeeEntity = employeeRepository.save(toSaveEntity);

        // ✔ Convert Entity → DTO and return response
        return modelMapper.map(savedEmployeeEntity, EmployeeDTO.class);
    }

    // ===========================================================================================
    // 4️⃣ UPDATE ENTIRE EMPLOYEE DATA (PUT)
    // ===========================================================================================
    public EmployeeDTO updateEmployeeById(Long employeeId, EmployeeDTO employeeDTO) {
        // ✔ Ensure employee exists in database, else throw exception
        isExistsByEmployeeId(employeeId);

        // ✔ Convert updated data DTO → Entity
        EmployeeEntity employeeEntity = modelMapper.map(employeeDTO, EmployeeEntity.class);

        // ✔ Ensure original ID remains same
        employeeEntity.setId(employeeId);

        // ✔ Save updated entity in DB
        EmployeeEntity savedEmployeeEntity = employeeRepository.save(employeeEntity);

        // ✔ Return converted DTO
        return modelMapper.map(savedEmployeeEntity, EmployeeDTO.class);
    }

    // ===========================================================================================
    // 5️⃣ CHECK EMPLOYEE EXISTS (Helper Utility Method)
    // ===========================================================================================
    public void isExistsByEmployeeId(Long employeeId) {
        // ✔ Check if ID exists using repository
        boolean exists = employeeRepository.existsById(employeeId);

        // ✔ If not found → throw a custom exception
        if (!exists) throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
    }

    // ===========================================================================================
    // 6️⃣ DELETE EMPLOYEE BY ID
    // ===========================================================================================
    public boolean deleteEmployeeById(Long employeeId) {
        // ✔ Ensure record exists
        isExistsByEmployeeId(employeeId);

        // ✔ Delete the record
        employeeRepository.deleteById(employeeId);

        return true; // Return success response
    }

    // ===========================================================================================
    // 7️⃣ PARTIAL UPDATE (PATCH REQUEST — Only update provided fields)
    // ===========================================================================================
    public EmployeeDTO updatePartialEmployeeById(Long employeeId, Map<String, Object> updates) {
        // ✔ Step 1: Validate employee exists
        isExistsByEmployeeId(employeeId);

        // ✔ Step 2: Get the existing entity
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).get();

        // ✔ Step 3: Loop through fields to update dynamically
        updates.forEach((field, value) -> {
            //  Use Spring's ReflectionUtils to find actual field inside the Entity class
            Field fieldToBeUpdated = ReflectionUtils.findRequiredField(EmployeeEntity.class, field);

            //  Allow access to private fields using reflection
            fieldToBeUpdated.setAccessible(true);

            //  Modify the field value dynamically at runtime
            ReflectionUtils.setField(fieldToBeUpdated, employeeEntity, value);
        });

        // ✔ Step 4: Save updated entity & return updated DTO
        return modelMapper.map(employeeRepository.save(employeeEntity), EmployeeDTO.class);
    }
}

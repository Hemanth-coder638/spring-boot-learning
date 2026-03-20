package com.hemanth.collegemanagement;

import com.hemanth.collegemanagement.dto.AdmissionRecordDto;
import com.hemanth.collegemanagement.dto.ProfessorDto;
import com.hemanth.collegemanagement.dto.StudentDto;
import com.hemanth.collegemanagement.dto.SubjectDto;
import com.hemanth.collegemanagement.entity.AdmissionRecordEntity;
import com.hemanth.collegemanagement.entity.ProfessorEntity;
import com.hemanth.collegemanagement.entity.StudentEntity;
import com.hemanth.collegemanagement.entity.SubjectEntity;
import com.hemanth.collegemanagement.repository.AdmissionRecordRepository;
import com.hemanth.collegemanagement.repository.ProfessorRepository;
import com.hemanth.collegemanagement.repository.StudentRepository;
import com.hemanth.collegemanagement.repository.SubjectRepository;
import com.hemanth.collegemanagement.service.ProfessorService;
import com.hemanth.collegemanagement.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class DataLoader implements CommandLineRunner {

    private final ProfessorService professorService;
    private final StudentService studentService;
    private final SubjectRepository subjectRepository;
    private final AdmissionRecordRepository admissionRecordRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    @Override
    public void run(String... args) {

        String token = "VALID_TOKEN";

        // 1. Create Professors
        ProfessorDto profA = professorService.createProfessor(
                ProfessorDto.builder()
                        .title("Prof A")
                        .subjectIds(Set.of())
                        .studentIds(Set.of())
                        .build(),
                token
        );

        ProfessorDto profB = professorService.createProfessor(
                ProfessorDto.builder()
                        .title("Prof B")
                        .subjectIds(Set.of())
                        .studentIds(Set.of())
                        .build(),
                token
        );

        // 2. Create Subjects
        ProfessorEntity profAEntity = professorRepository.findById(profA.getId())
                .orElseThrow(() -> new RuntimeException("Professor A not found"));

        ProfessorEntity profBEntity = professorRepository.findById(profB.getId())
                .orElseThrow(() -> new RuntimeException("Professor B not found"));

        SubjectEntity math = new SubjectEntity();
        math.setTitle("Math");
        math.setProfessor(profAEntity);

        SubjectEntity physics = new SubjectEntity();
        physics.setTitle("Physics");
        physics.setProfessor(profBEntity);

        subjectRepository.save(math);
        subjectRepository.save(physics);

        // 3. Create Students (DTO → Service)
        StudentDto alice = studentService.createStudent(
                StudentDto.builder()
                        .name("Alice")
                        .professorIds(Set.of())
                        .subjectIds(Set.of())
                        .build(),
                token
        );

        StudentDto bob = studentService.createStudent(
                StudentDto.builder()
                        .name("Bob")
                        .professorIds(Set.of())
                        .subjectIds(Set.of())
                        .build(),
                token
        );

        // 4. Add Relationships (Service Layer)
        studentService.addProfessorToStudent(alice.getId(), profA.getId(), token);
        studentService.addSubjectToStudent(alice.getId(), math.getId(), token);

        studentService.addProfessorToStudent(bob.getId(), profB.getId(), token);
        studentService.addSubjectToStudent(bob.getId(), physics.getId(), token);


        // 5. Admission Records
        StudentEntity aliceEntity = studentRepository.findById(alice.getId())
                .orElseThrow(() -> new RuntimeException("Alice not found"));

        StudentEntity bobEntity = studentRepository.findById(bob.getId())
                .orElseThrow(() -> new RuntimeException("Bob not found"));

        AdmissionRecordEntity admission1 = new AdmissionRecordEntity();
        admission1.setFees(5000);
        admission1.setStudent(aliceEntity);

        AdmissionRecordEntity admission2 = new AdmissionRecordEntity();
        admission2.setFees(10000);
        admission2.setStudent(bobEntity);

        admissionRecordRepository.save(admission1);
        admissionRecordRepository.save(admission2);

        // 6. Read (AOP Testing)
        System.out.println(professorService.getAllProfessors(token));
        System.out.println(studentService.getAllStudents(token));

        System.out.println("DataLoader executed successfully");
    }
}
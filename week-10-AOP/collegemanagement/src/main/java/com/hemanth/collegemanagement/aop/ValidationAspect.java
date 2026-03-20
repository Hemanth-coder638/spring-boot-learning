package com.hemanth.collegemanagement.aop;


import com.hemanth.collegemanagement.dto.ProfessorDto;
import com.hemanth.collegemanagement.dto.StudentDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidationAspect {

    @Before("@annotation(com.hemanth.collegemanagement.aop.annotation.ValidateDto)")
    public void validate(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {

            if (arg == null) {
                throw new RuntimeException("Invalid input: Null value passed");
            }

            // StudentDto Validation
            if (arg instanceof StudentDto student) {

                if (student.getName() == null || student.getName().trim().isEmpty()) {
                    throw new RuntimeException("Student name cannot be null or empty");
                }

//                if (student.getProfessorIds() != null && student.getProfessorIds().contains(null)) {
//                    throw new RuntimeException("Professor IDs cannot contain null values");
//                }
//
//                if (student.getSubjectIds() != null && student.getSubjectIds().contains(null)) {
//                    throw new RuntimeException("Subject IDs cannot contain null values");
//                }
            }

            //  ProfessorDto Validation
            if (arg instanceof ProfessorDto professor) {

                if (professor.getTitle() == null || professor.getTitle().trim().isEmpty()) {
                    throw new RuntimeException("Professor title cannot be null or empty");
                }

//                if (professor.getStudentIds() != null && professor.getStudentIds().contains(null)) {
//                    throw new RuntimeException("Student IDs cannot contain null values");
//                }
//
//                if (professor.getSubjectIds() != null && professor.getSubjectIds().contains(null)) {
//                    throw new RuntimeException("Subject IDs cannot contain null values");
//                }
            }
        }
    }
}
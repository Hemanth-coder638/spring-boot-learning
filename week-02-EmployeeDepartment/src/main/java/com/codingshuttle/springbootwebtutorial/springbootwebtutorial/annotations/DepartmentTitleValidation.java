package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.annotations;

import jakarta.validation.*;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Constraint(validatedBy = DepartmentTitleValidator.class)
@Documented
public @interface DepartmentTitleValidation {
    String message() default "Title should contain uppercase letter";
}

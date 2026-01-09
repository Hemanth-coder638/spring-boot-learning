package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DepartmentTitleValidator implements ConstraintValidator<DepartmentTitleValidation,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.length()>=3 && value.length()<20)
            return true;
        return false;
    }
}

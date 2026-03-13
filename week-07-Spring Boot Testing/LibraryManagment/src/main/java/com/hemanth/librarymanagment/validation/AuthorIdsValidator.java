package com.hemanth.librarymanagment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;

public class AuthorIdsValidator implements ConstraintValidator<AuthorIdsValidate, Set<?>> {
    @Override
    public boolean isValid(Set<?> value, ConstraintValidatorContext context) {
        if(value==null || value.isEmpty()){
            return true;
        }
        for(Object obj:value){
            if(!(obj instanceof Number))
                return false;
        }
        return true;
    }
}

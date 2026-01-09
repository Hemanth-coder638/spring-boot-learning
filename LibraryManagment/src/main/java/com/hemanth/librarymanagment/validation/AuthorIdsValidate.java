package com.hemanth.librarymanagment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Constraint(validatedBy = {AuthorIdsValidator.class})
public @interface AuthorIdsValidate {
    String message() default "AuthorIds should be numbers";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}

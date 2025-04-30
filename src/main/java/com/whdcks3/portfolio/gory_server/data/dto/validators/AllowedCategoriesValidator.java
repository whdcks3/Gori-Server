package com.whdcks3.portfolio.gory_server.data.dto.validators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.whdcks3.portfolio.gory_server.data.dto.interfaces.AllowedCategories;

public class AllowedCategoriesValidator implements ConstraintValidator<AllowedCategories, String> {
    private String[] allowedCategories;

    @Override
    public void initialize(AllowedCategories constraintAnnotation) {
        this.allowedCategories = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Arrays.asList(allowedCategories).contains(value);
    }
}

package com.whdcks3.portfolio.gory_server.data.dto.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.whdcks3.portfolio.gory_server.data.dto.validators.AllowedCategoriesValidator;

@Constraint(validatedBy = AllowedCategoriesValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedCategories {
    String message() default "사용 가능한 카테고리가 아닙니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values();
}

package com.github.nramc.dev.journey.api.core.validation;

import com.github.nramc.dev.journey.api.core.validation.validator.ValidateVisibilitiesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateVisibilitiesValidator.class)
@Documented
public @interface ValidateVisibilities {
    String ERROR_MESSAGE = "visibilities invalid";

    String message() default ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

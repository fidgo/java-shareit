package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEndValidation {
    String message() default "End Before Start!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

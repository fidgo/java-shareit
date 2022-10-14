package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEndValidation, BookingDto> {
    @Override
    public void initialize(StartBeforeEndValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDto value, ConstraintValidatorContext context) {
        return (value != null) && (value.getStart().isBefore(value.getEnd()));
    }
}

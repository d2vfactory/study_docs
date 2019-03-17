package com.d2vfactory.resttodolist.validator;

import com.d2vfactory.resttodolist.model.common.Status;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class StatusValidator implements ConstraintValidator<StatusConstraint, String> {
    @Override
    public boolean isValid(String status, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(Status.values())
                .anyMatch(x -> x.name().equals(status.toUpperCase()));
    }
}

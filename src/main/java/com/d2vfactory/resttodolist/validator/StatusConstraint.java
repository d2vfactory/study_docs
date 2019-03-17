package com.d2vfactory.resttodolist.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusConstraint {
    String message() default "Invalid Todo Status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

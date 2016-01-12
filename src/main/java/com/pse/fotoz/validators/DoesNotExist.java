package com.pse.fotoz.validators;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Creates a DoesNotExist constraint able to be applied to field attributes
 * 
 * @author Gijs
 */
@Documented
@Constraint(validatedBy = DoesNotExistValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface DoesNotExist {
    
    String message() default "{0} bestaat al";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    Class entity();
    
    String field();
}

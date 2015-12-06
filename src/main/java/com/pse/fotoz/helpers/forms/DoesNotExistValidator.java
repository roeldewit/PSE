package com.pse.fotoz.helpers.forms;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Gijs
 */
public class DoesNotExistValidator implements ConstraintValidator<DoesNotExist, String> {
    private Class entity;
    private String field;
     
    /**
     * sets fields of DoesNotExistValidator
     * @param constraint constraint as applied in entity
     */
    @Override
    public void initialize(DoesNotExist constraint) {
        entity = constraint.entity();
        field = constraint.field();
    }
    
    /**
     * Checks if a certain value is present within a column (field) 
     * of an entity in the database
     * @param value the value to be checked
     * @param context provides contextual data of applied constraint
     * @return true if validation succeeded
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !HibernateEntityHelper.find(entity, field, value).stream().
                findAny().
                isPresent();
    }

}

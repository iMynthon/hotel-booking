package com.example.hotelbookingapplication.validation.service;

import com.example.hotelbookingapplication.validation.PaginationFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorService implements ConstraintValidator<CheckValidPagination, PaginationFilter> {
    @Override
    public boolean isValid(PaginationFilter filter, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (filter.getPageNumber() < 0) {
            assemblingMessage(context,"Значение pageNumber задано некорректно: " + filter.getPageNumber());
            return false;

        } else if (filter.getPageSize() < 0) {
            assemblingMessage(context,"Значение pageSize задано некорректно: " + filter.getPageSize());
            return false;
        }
        return true;
    }

    private void assemblingMessage(ConstraintValidatorContext context, String message){
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}

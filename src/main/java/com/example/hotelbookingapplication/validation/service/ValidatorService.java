package com.example.hotelbookingapplication.validation.service;

import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import com.example.hotelbookingapplication.validation.filter.ValidatorFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidatorService implements ConstraintValidator<CheckValidPagination, ValidatorFilter>{

    private List<String> getFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(ValidatorFilter filter, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if(!checkNameRequest(context)){
            return false;
        }
        else if (filter.getPageNumber() < 0) {
            assemblingMessage(context,"Значение pageNumber задано некорректно: " + filter.getPageNumber());
            return false;

        } else if (filter.getPageSize() < 0) {
            assemblingMessage(context,"Значение pageSize задано некорректно: " + filter.getPageSize());
            return false;
        }
        return true;
    }

    public boolean checkNameRequest(ConstraintValidatorContext context) {
        List<String> fields = getFieldNames(HotelValidatorFilter.class);
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getParameterMap()
                .entrySet()
                .stream()
                .allMatch(paramName -> {
                    if(!fields.contains(paramName.getKey())){
                        assemblingMessage(context,String.format("Неверный параметр запроса %s. Параметры запроса: %s",paramName.getKey(),fields));
                        return false;
                    }
                    return true;
                });
    }

    private void assemblingMessage(ConstraintValidatorContext context, String message){
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}

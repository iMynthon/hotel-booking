package com.example.hotelbookingapplication.validation.service;

import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import com.example.hotelbookingapplication.validation.filter.RoomValidatorFilter;
import com.example.hotelbookingapplication.validation.filter.ValidatorFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidatorService implements ConstraintValidator<CheckValidPagination, ValidatorFilter>{

    private static final List<String> ALL_VALID_PARAMS = fieldsFilterClass();

    private static List<String> fieldsFilterClass(){
        return Stream.of(
                ValidatorFilter.class,
                HotelValidatorFilter.class,
                RoomValidatorFilter.class
        ).map(clazz -> Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList()))
                .flatMap(List::stream)
                .distinct()
                .toList();
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
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getParameterMap()
                .entrySet()
                .stream()
                .allMatch(paramName -> {
                    if(!ALL_VALID_PARAMS.contains(paramName.getKey())){
                        assemblingMessage(context,String.format("Неверный параметр запроса %s. Параметры запроса: %s"
                                ,paramName.getKey(),ALL_VALID_PARAMS));
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

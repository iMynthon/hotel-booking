package com.example.hotelbookingapplication.logging.request;

import com.example.hotelbookingapplication.validation.filter.ValidatorFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RequestLogging extends OncePerRequestFilter {

    private final ValidatorFilter validatorFilter = new ValidatorFilter();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Request: {} - {}",request.getMethod(),request.getRequestURL());
        log.info("Param: {}",parameterRequest(request));
        filterChain.doFilter(request,response);
    }

    private String parameterRequest(HttpServletRequest request){
        return request.getParameterMap().isEmpty() ? validatorFilter.toStringSizeAndNumber() : request.getParameterMap().entrySet().stream()
                .map(p -> String.format("%s=%s",p.getKey(),String.join(",",p.getValue())))
                .collect(Collectors.joining("&"));

    }
}

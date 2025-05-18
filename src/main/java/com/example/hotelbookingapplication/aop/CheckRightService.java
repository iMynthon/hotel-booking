package com.example.hotelbookingapplication.aop;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.model.jpa.Authority;
import com.example.hotelbookingapplication.model.jpa.RoleType;
import com.example.hotelbookingapplication.model.jpa.User;
import com.example.hotelbookingapplication.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckRightService {

    private final UserRepository userRepository;

    @Before("@annotation(CheckUserRights)")
    public void isOwner(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = findByUsername(authentication.getName());
        Object requestParam = getRequestUser(joinPoint);
        if (requestParam instanceof Integer) {
            checkAccessRightById((Integer) requestParam, currentUser);
        } else if (requestParam instanceof String) {
            checkAccessRightsByUsername((String) requestParam, currentUser);
        }
    }

    private void checkAccessRightById(Integer id, User currentUser) {
        User requestUser = findById(id);
        if (!currentUser.equals(requestUser) && !getRole(currentUser).equals(RoleType.ROLE_ADMIN)) {
            throw new AccessDeniedException("Выполнять операции с другими пользователя с ролью User запрещено");
        }
    }

    private void checkAccessRightsByUsername(String username, User currentUser) {
        User requestUser = findByUsername(username);
        if (!currentUser.equals(requestUser) && !getRole(currentUser).equals(RoleType.ROLE_ADMIN)) {
            throw new AccessDeniedException("Выполнять операции с другими пользователя с ролью User запрещено");
        }
    }


    private Object getRequestUser(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Integer || arg instanceof String)
                .findFirst().orElseThrow();
    }

    private RoleType getRole(User currentUser) {
        return currentUser.getRoles().stream()
                .map(Authority::getRole)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Метод с аннотацией @CheckUserRights должен содержать параметр ID (Integer) или имя пользователя (String)"));
    }

    private User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User под id - {%s} - не найдем", id)));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("User под таким именем - {%s} не зарегистрирован", username)));
    }
}

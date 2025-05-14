package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.AuthorityUserException;
import com.example.hotelbookingapplication.model.Authority;
import com.example.hotelbookingapplication.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public Authority save(Authority authority){
        return authorityRepository.save(authority);
    }

    public Authority findByUserId(Integer id){
        return authorityRepository.findByUserId(id)
                .orElseThrow(() -> new AuthorityUserException(
                        String.format("Пользователь под таким id - {%s} не авторизован",id)));
    }
}

package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.DuplicateDataException;
import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.UserMapper;
import com.example.hotelbookingapplication.model.Authority;
import com.example.hotelbookingapplication.model.RoleType;
import com.example.hotelbookingapplication.model.User;
import com.example.hotelbookingapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthorityService authorityService;

    private final PasswordEncoder encoder;

    private final UserMapper userMapper;

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(String.format("User под id - {%s} - не найдем",id)));
    }
    @Named("findByUsername")
    public User findByUsername(String username){
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(()->
                new EntityNotFoundException(
                        String.format("User под таким именем - {%s} не зарегистрирован",username)));
    }

    public User save(RoleType role,User entity) {
        checkDuplicateUserDataSave(entity);
        Authority authority = authorityService.save(Authority.builder()
                .role(role)
                .user(entity).build());
        entity.getRoles().add(authority);
        entity.setPassword(encoder.encode(entity.getPassword()));
        return userRepository.save(entity);
    }

    public User update(RoleType role, User entity) {
        User exists = findById(entity.getId());
        checkDuplicateUserDataSave(entity);
        Authority authority = authorityService.findByUserId(entity.getId());
        authority.setRole(role == null ? authority.getRole() : role);
        userMapper.update(exists,entity);
        exists.setPassword(encoder.encode(exists.getPassword()));
        return userRepository.save(exists);
    }

    public void deleteById(Integer id) {
         if(!userRepository.existsById(id)){
             throw new EntityNotFoundException(String.format("User под id:{%s} - не найдем",id));
         }
         userRepository.deleteById(id);

    }

    private void checkDuplicateUserDataSave(User user){
       if(userRepository.existsByUsernameIgnoreCase(user.getUsername())){
           throw new DuplicateDataException(
                   String.format("User с таким именем - {%s} уже зарегистрирован",user.getUsername()));
       }
       else if(userRepository.existsByEmailIgnoreCase(user.getEmail())){
           throw new DuplicateDataException(
                   String.format("User с таким email - {%s} уже зарегистрирован",user.getUsername()));
       }
    }
}

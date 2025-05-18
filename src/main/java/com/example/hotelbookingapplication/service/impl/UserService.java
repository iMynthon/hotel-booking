package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.DuplicateDataException;
import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.UserMapper;
import com.example.hotelbookingapplication.model.jpa.Authority;
import com.example.hotelbookingapplication.model.jpa.RoleType;
import com.example.hotelbookingapplication.model.jpa.User;
import com.example.hotelbookingapplication.model.kafka.UserEvent;
import com.example.hotelbookingapplication.repository.jpa.UserRepository;
import com.example.hotelbookingapplication.service.mongodb.RegistrationUserStatsService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthorityService authorityService;

    private final RegistrationUserStatsService registrationUserStatsService;

    private final PasswordEncoder encoder;

    private final UserMapper userMapper;

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

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
        User newUser = userRepository.save(entity);
        createSendMessageUser(newUser.getId());
        return newUser;
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
         registrationUserStatsService.deleteById(id);
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

    private void createSendMessageUser(Integer id){
        String userRegisteredTopic = "user_registered";
        kafkaTemplate.send(userRegisteredTopic,
                "user_registered" + System.currentTimeMillis(),
                new UserEvent(id,LocalDateTime.now()));
    }
}

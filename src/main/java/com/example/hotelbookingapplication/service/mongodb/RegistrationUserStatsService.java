package com.example.hotelbookingapplication.service.mongodb;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.model.jpa.User;
import com.example.hotelbookingapplication.model.mongodb.RegistrationUserStats;
import com.example.hotelbookingapplication.repository.jpa.UserRepository;
import com.example.hotelbookingapplication.repository.mongodb.RegistrationUserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationUserStatsService {

    private final RegistrationUserStatsRepository repository;

    private final UserRepository userRepository;

    private final CSVFormat mongoDBFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "registered_time","username","email")
            .build();

    private final Path filePath = Paths.get("data/statistics_users_registration.csv");

    public void dataRecordToFile() {
        try(BufferedWriter writer = Files.newBufferedWriter(filePath)){
            CSVPrinter csvPrinter = new CSVPrinter(writer, mongoDBFormat);
            for(RegistrationUserStats stats : repository.findAll()){
                stats.setUser(findById(stats.getId()));
                csvPrinter.printRecord(stats.getId(), stats.getRegisteredTime()
                        ,stats.getUser().getUsername(),stats.getUser().getEmail());
            }
        } catch (IOException ie){
            log.info("Ошибка записи в файл: {} ",ie.getMessage());
        }
    }

    public void deleteById(Integer id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException(String.format("Запись User под id:{%s} - в Mongo DB не найдена",id));
        }
        log.info("Удаление из MongoDB пользователя с id:{}",id);
        repository.deleteById(id);
    }

    public void save(RegistrationUserStats registrationUserStats){
        log.info("RegisteredUserIdService - save to Mongo DB");
        User currentUser = findById(registrationUserStats.getId());
        registrationUserStats.setUser(currentUser);
        repository.save(registrationUserStats);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(String.format("User под id - {%s} - не найдем",id)));
    }
}

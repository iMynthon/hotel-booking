package com.example.hotelbookingapplication.repository.mongodb;

import com.example.hotelbookingapplication.model.mongodb.RegistrationUserStats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationUserStatsRepository extends MongoRepository<RegistrationUserStats,Integer> {
}

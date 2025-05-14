package com.example.hotelbookingapplication.repository;

import com.example.hotelbookingapplication.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Integer> {

    Optional<Authority> findByUserId(Integer userid);
}

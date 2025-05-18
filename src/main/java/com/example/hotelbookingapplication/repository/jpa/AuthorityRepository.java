package com.example.hotelbookingapplication.repository.jpa;

import com.example.hotelbookingapplication.model.jpa.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Integer> {

    Optional<Authority> findByUserId(Integer userid);
}

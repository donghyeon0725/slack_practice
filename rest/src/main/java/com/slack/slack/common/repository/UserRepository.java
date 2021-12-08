package com.slack.slack.common.repository;

import com.slack.slack.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u join fetch u.userRoles r join fetch r.role where u.email = :email")
    Optional<User> findByEmail(String email);

    Optional<List<User>> findTop5ByEmailContaining(String email);
}

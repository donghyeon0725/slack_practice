package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 성능 개선을 위해서 join 쿼리를 직접 넘깁니다.
    @Query("select a from Account a join fetch a.accountRoles r join fetch r.role where a.username = :name")
    Optional<Account> findByUsername(String name);
}

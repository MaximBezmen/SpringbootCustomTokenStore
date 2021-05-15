package com.spring.SpringbootCustomTokenStore.repository;

import com.spring.SpringbootCustomTokenStore.entites.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(Long id);

    Optional<Account> findByLoginAndPassword(String login, String password);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByLogin(String login);

    Optional<Account> findByLoginOrEmail(String login, String email);

}

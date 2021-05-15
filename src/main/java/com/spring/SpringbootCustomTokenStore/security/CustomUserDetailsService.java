package com.spring.SpringbootCustomTokenStore.security;

import com.spring.SpringbootCustomTokenStore.entites.Account;
import com.spring.SpringbootCustomTokenStore.repository.AccountRepository;
import javassist.tools.web.BadHttpRequest;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public CustomUserDetailsService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @SneakyThrows
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) {

        Optional<Account> accountOptional = accountRepository.findByLoginOrEmail(login, login);
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + login);
        }

        return UserPrincipal.create(accountOptional.get());
    }

    @SneakyThrows
    @Transactional
    public UserDetails loadUserById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                BadHttpRequest::new
        );
        return UserPrincipal.create(account);
    }
}

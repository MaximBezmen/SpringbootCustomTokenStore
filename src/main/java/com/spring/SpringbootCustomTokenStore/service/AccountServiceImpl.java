package com.spring.SpringbootCustomTokenStore.service;


import com.spring.SpringbootCustomTokenStore.entites.Account;
import com.spring.SpringbootCustomTokenStore.entites.Role;
import com.spring.SpringbootCustomTokenStore.exception.BadRequestException;
import com.spring.SpringbootCustomTokenStore.repository.AccountRepository;
import com.spring.SpringbootCustomTokenStore.repository.RoleRepository;
import com.spring.SpringbootCustomTokenStore.service.dto.AccountDto;
import com.spring.SpringbootCustomTokenStore.service.dto.mapper.AccountMapper;
import com.spring.SpringbootCustomTokenStore.util.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public AccountDto getUserByLoginAndPassword(AccountDto accountDto) {
        Optional<Account> accountOptional = accountRepository.findByLoginAndPassword(accountDto.getLogin(), accountDto.getPassword());
        if (accountOptional.isEmpty()) {
            throw new BadRequestException("User with not found.");
        }
        return accountMapper.toDto(accountOptional.get());
    }

    @Transactional
    @Override
    public AccountDto registerNewUserAccount(AccountDto accountDto) {
        Optional<Account> accountOptional = accountRepository.findByEmail(accountDto.getEmail());
        if (accountOptional.isPresent()) {
            throw new BadRequestException(accountDto.getEmail());
        } else {
            accountOptional = accountRepository.findByLogin(accountDto.getLogin());
            if (accountOptional.isPresent()) {
                throw new BadRequestException(accountDto.getLogin());
            }
        }
        Account accountEntity = accountMapper.toEntity(accountDto);
        accountEntity.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        Role roleEntity = roleRepository.findByRoleName(RoleType.USER).orElseThrow(
                () -> new BadRequestException("Role user not found."));
        accountEntity.setRole(roleEntity);
        accountEntity = accountRepository.save(accountEntity);
        return accountMapper.toDto(accountEntity);
    }

    @Override
    public AccountDto getUserById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new BadRequestException("");
        }
        return accountMapper.toDto(accountOptional.get());
    }


    @Override
    public Page<AccountDto> getAllUsersForAdmin(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll(pageable);
        return accountPage.map(accountMapper::toDto);
    }

}

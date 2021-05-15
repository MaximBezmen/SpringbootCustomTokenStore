package com.spring.SpringbootCustomTokenStore.service;


import com.spring.SpringbootCustomTokenStore.service.dto.AccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AccountService {

    AccountDto getUserByLoginAndPassword(AccountDto accountDto);

    AccountDto registerNewUserAccount(AccountDto accountDto);

    AccountDto getUserById(Long id);

    Page<AccountDto> getAllUsersForAdmin(Pageable pageable);

}

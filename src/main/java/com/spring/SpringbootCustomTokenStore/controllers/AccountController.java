package com.spring.SpringbootCustomTokenStore.controllers;


import com.spring.SpringbootCustomTokenStore.service.AccountService;
import com.spring.SpringbootCustomTokenStore.service.JWTTokenStoreService;
import com.spring.SpringbootCustomTokenStore.service.dto.AccountDto;
import com.spring.SpringbootCustomTokenStore.service.dto.TokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AccountController {

    private final AccountService accountService;
    private final JWTTokenStoreService jwtTokenStoreService;


    AccountController(AccountService accountService, JWTTokenStoreService jwtTokenStoreService) {
        this.accountService = accountService;
        this.jwtTokenStoreService = jwtTokenStoreService;
    }


    @PostMapping("/users/registration")
    public ResponseEntity<AccountDto> registrationNewUser(@RequestBody final AccountDto accountDto) {
        return ResponseEntity.ok().body(accountService.registerNewUserAccount(accountDto));
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<AccountDto> getUserById(@PathVariable final Long id) {
        return ResponseEntity.ok().body(accountService.getUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AccountDto>> getAllUsersForAdmin(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok().body(accountService.getAllUsersForAdmin(pageable));
    }


    @PostMapping("/users/revokeToken")
    public ResponseEntity<Void> revokeToken(@RequestBody final TokenDto token) {
        jwtTokenStoreService.revokeToken(token.getToken());
        return ResponseEntity.ok().build();
    }
}

package com.spring.SpringbootCustomTokenStore.service.dto;

import com.spring.SpringbootCustomTokenStore.util.RoleType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AccountDto {
    private Long id;
    @NotNull
    @NotEmpty
    private String login;
    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String email;

    private RoleType role;
}

package com.spring.SpringbootCustomTokenStore.service.dto.mapper;


import com.spring.SpringbootCustomTokenStore.entites.Account;
import com.spring.SpringbootCustomTokenStore.service.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "role.roleName")
    AccountDto toDto(Account entity);

    @Mapping(target = "id", ignore = true)
    Account toEntity(AccountDto dto);
}

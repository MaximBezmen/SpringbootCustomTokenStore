package com.spring.SpringbootCustomTokenStore.repository;

import com.spring.SpringbootCustomTokenStore.entites.JWTTokenStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JWTTokenStoreRepository extends JpaRepository<JWTTokenStore, Long> {

    JWTTokenStore findByToken(String token);

    JWTTokenStore findByAccount_Email(String email);

}

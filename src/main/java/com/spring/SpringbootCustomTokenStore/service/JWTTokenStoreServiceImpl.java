package com.spring.SpringbootCustomTokenStore.service;

import com.spring.SpringbootCustomTokenStore.entites.Account;
import com.spring.SpringbootCustomTokenStore.entites.JWTTokenStore;
import com.spring.SpringbootCustomTokenStore.repository.AccountRepository;
import com.spring.SpringbootCustomTokenStore.repository.JWTTokenStoreRepository;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.spring.SpringbootCustomTokenStore.security.SecurityConstants.SECRET;


@Service
public class JWTTokenStoreServiceImpl implements JWTTokenStoreService {
    private final AccountRepository accountRepository;
    private final JWTTokenStoreRepository jwtTokenStoreRepository;
    private volatile ConcurrentHashMap<String, String> localTokenStore;

    public JWTTokenStoreServiceImpl(AccountRepository accountRepository, JWTTokenStoreRepository jwtTokenStoreRepository) {
        this.accountRepository = accountRepository;
        this.jwtTokenStoreRepository = jwtTokenStoreRepository;
        downlandTokenFromDB();

    }

    private void downlandTokenFromDB() {
        List<JWTTokenStore> jwtTokenStoreList = jwtTokenStoreRepository.findAll();
        localTokenStore = new ConcurrentHashMap<>();
        jwtTokenStoreList.forEach(jwtTokenStore -> localTokenStore.put(jwtTokenStore.getAccount().getEmail(), jwtTokenStore.getToken()));
    }

    @Override
    public boolean saveToken(String token, Long userId, Date expiryDate) {
        boolean saveToken = false;
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()){
            JWTTokenStore jwtTokenStore = new JWTTokenStore();
            jwtTokenStore.setToken(token);
            jwtTokenStore.setAccount(optionalAccount.get());
            jwtTokenStore.setValidate(true);
            jwtTokenStore.setExpiryDate(expiryDate);
            jwtTokenStoreRepository.save(jwtTokenStore);
            saveToken = true;
        }
        return saveToken;
    }

    @Override
    public boolean revokeToken(String token) {
        boolean revokeToken = false;
        JWTTokenStore jwtTokenStoreEntity = jwtTokenStoreRepository.findByToken(token);
        if (jwtTokenStoreEntity != null){
            jwtTokenStoreRepository.delete(jwtTokenStoreEntity);
            localTokenStore.remove(jwtTokenStoreEntity.getAccount().getEmail());
            revokeToken = true;
        }
        return revokeToken;
    }

    @Override
    public boolean checkValidateToken(String token) {
        JWTTokenStore jwtTokenStoreEntity = jwtTokenStoreRepository.findByToken(token);
        return jwtTokenStoreEntity == null;
    }

    @Override
    public boolean checkValidateTokenInLocal(String token, String email) {
        String jwtTokenStoreEntity = localTokenStore.get(email);
        return jwtTokenStoreEntity == null || !jwtTokenStoreEntity.equals(token);
    }

    @Override
    public String findToken(String email) {
        JWTTokenStore jwtTokenStoreEntity = jwtTokenStoreRepository.findByAccount_Email(email);
        if (jwtTokenStoreEntity == null){
            return null;
        } else {
            if (!validateToken(jwtTokenStoreEntity.getToken())){
                jwtTokenStoreRepository.delete(jwtTokenStoreEntity);
                localTokenStore.remove(jwtTokenStoreEntity.getAccount().getEmail());
                return null;
            }
        }
        return jwtTokenStoreEntity.getToken();
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
            //return jwtTokenStoreService.checkValidateToken(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ignored) {

        }
        return false;
    }
}

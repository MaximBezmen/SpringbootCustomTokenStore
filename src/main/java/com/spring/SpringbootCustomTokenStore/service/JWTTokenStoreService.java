package com.spring.SpringbootCustomTokenStore.service;

import java.util.Date;

public interface JWTTokenStoreService {

    boolean saveToken(String token, Long userId, Date expiryDate);

    boolean revokeToken(String token);

    boolean checkValidateToken(String token);

    boolean checkValidateTokenInLocal(String token, String email);

    String findToken(String email);

    boolean validateToken(String authToken);

}

package com.spring.SpringbootCustomTokenStore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.SpringbootCustomTokenStore.service.JWTTokenStoreService;
import com.spring.SpringbootCustomTokenStore.service.dto.AccountDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.spring.SpringbootCustomTokenStore.security.SecurityConstants.*;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTTokenStoreService jwtTokenStoreService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTTokenStoreService jwtTokenStoreService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenStoreService = jwtTokenStoreService;
        setFilterProcessesUrl("/users/login");
    }


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            AccountDto accountDto = new ObjectMapper().readValue(request.getInputStream(), AccountDto.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    accountDto.getLogin(),
                    accountDto.getPassword(),
                    new ArrayList<>()));
        } catch (IOException ex) {
            log.info("Responding with unauthorized error. Message - " + ex.getMessage());
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        String token;
        String oldToken =checkToken(authResult);
        if ( oldToken == null){
            token = createToken(authResult);
        }else {
            token = oldToken;
        }


        String body = ((UserPrincipal) authResult.getPrincipal()).getId() + " " + TOKEN_PREFIX + token;
        response.getWriter().write(body);
        response.getWriter().flush();
    }

    private String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        String token = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        jwtTokenStoreService.revokeToken(userPrincipal.getEmail());
        jwtTokenStoreService.saveToken(token, userPrincipal.getId(), expiryDate);
        return token;
    }

    private String checkToken (Authentication authResult){
        UserPrincipal userPrincipal = ((UserPrincipal) authResult.getPrincipal());
        String token = jwtTokenStoreService.findToken(userPrincipal.getEmail());
        return  token;
    }
}

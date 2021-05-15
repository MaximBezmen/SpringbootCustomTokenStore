package com.spring.SpringbootCustomTokenStore.config;


import com.spring.SpringbootCustomTokenStore.security.CustomUserDetailsService;
import com.spring.SpringbootCustomTokenStore.security.JWTAuthenticationFilter;
import com.spring.SpringbootCustomTokenStore.security.JWTAuthorizationFilter;
import com.spring.SpringbootCustomTokenStore.security.RestAuthenticationEntryPoint;
import com.spring.SpringbootCustomTokenStore.service.JWTTokenStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.spring.SpringbootCustomTokenStore.security.SecurityConstants.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTTokenStoreService jwtTokenStoreService;

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, JWTTokenStoreService jwtTokenStoreService) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenStoreService = jwtTokenStoreService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .formLogin()
                .disable()
                .logout()
                .disable()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SING_UP_URL ).permitAll()
                .antMatchers(HttpMethod.GET, "/users/*", REVOKE_URL).hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/users/*", REVOKE_URL).hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET,  "/users").hasAnyAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .userDetailsService(customUserDetailsService)
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtTokenStoreService))
                .addFilterBefore(new JWTAuthorizationFilter(customUserDetailsService, jwtTokenStoreService), UsernamePasswordAuthenticationFilter.class)
                //this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}

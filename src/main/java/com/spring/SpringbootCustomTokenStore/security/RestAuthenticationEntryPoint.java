package com.spring.SpringbootCustomTokenStore.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    final Logger LOGGER = Logger.getLogger(String.valueOf(RestAuthenticationEntryPoint.class));

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        LOGGER.warning("Responding with unauthorized error. Message - " + e.getMessage());
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                e.getLocalizedMessage());
    }
}

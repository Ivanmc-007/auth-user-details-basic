package com.ivan.userservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.userservice.domain.Role;
import com.ivan.userservice.utils.SecretTokenKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Описывает логику аутентификации (что я это я)
 */
@Log4j2
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final SecretTokenKey secretTokenKey;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // TODO: remove info
        log.info("Username is: {}", username);
        log.info("Password is: {}", password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        // добавляем ранее сгенерированный секрет
        Algorithm algorithm = Algorithm.HMAC256(secretTokenKey.getInstanceBytes());

        String access_token = JWT.create()
                .withSubject(user.getUsername())
                // установить жизнь токена на 10 мин
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                // кто издатель токена (кто выпустил ... как на заводе)
                .withIssuer(request.getRequestURI())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                // установить жизнь refresh токена на 30 мин
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                // кто издатель токена (кто выпустил ... как на заводе)
                .withIssuer(request.getRequestURI())
                .sign(algorithm);
        /*response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);*/
        Map<String, Object> mapResp = new HashMap<>();
        mapResp.put("access_token", access_token);
        mapResp.put("refresh_token", refresh_token);
        mapResp.put("roles", Collections.singletonList(Role.builder().id(1L).name("ROLE_USER").build()));
        // изменить тип возвращаемого контента на json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), mapResp);
    }

}

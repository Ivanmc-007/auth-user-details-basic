package com.ivan.userservice.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.userservice.domain.AppUser;
import com.ivan.userservice.domain.Role;
import com.ivan.userservice.service.app_user.AppUserService;
import com.ivan.userservice.utils.SecretTokenKey;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    private final SecretTokenKey secretTokenKey;

    @GetMapping("/users")
    public List<AppUser> findAppUsers() {
        return appUserService.findAppUsers();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(secretTokenKey.getInstanceBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();

                AppUser appUser = appUserService.findByUsername(username).orElse(null);

                String access_token = JWT.create()
                        .withSubject(appUser.getUsername())
                        // установить жизнь токена на 10 мин
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        // кто издатель токена (кто выпустил ... как на заводе)
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", appUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, Object> mapResp = new HashMap<>();
                mapResp.put("access_token", access_token);
                mapResp.put("refresh_token", refresh_token);
                mapResp.put("roles", Collections.singletonList(Role.builder().id(1L).name("ROLE_USER").build()));
                // изменить тип возвращаемого контента на json
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), mapResp);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, Object> mapRespErr = new HashMap<>();
                mapRespErr.put("error_message", e.getMessage());
                // изменить тип возвращаемого контента на json
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), mapRespErr);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}

@Data
class Form {
    private String username;
    private String password;
}
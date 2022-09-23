package com.ivan.userservice.security;

import com.ivan.userservice.filter.CustomAuthenticationFilter;
import com.ivan.userservice.filter.CustomAuthorizationFilter;
import com.ivan.userservice.utils.SecretTokenKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final SecretTokenKey secretTokenKey;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${springdoc.swagger-ui.path}")
    private String pathSwaggerUI;

    @Value("${springdoc.api-docs.path}")
    private String pathApiDocs;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // TODO: add normal encoder
        auth.userDetailsService(userDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), this.secretTokenKey);
        // изменяем точку входа
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        // открыть доступ для точки входа и для обновления токена
        http.authorizeRequests().antMatchers("/api/login", "/api/token/refresh").permitAll();
        // открыть доступ для документации swagger
        http.authorizeRequests().antMatchers(pathSwaggerUI + "/**").permitAll();
        http.authorizeRequests().antMatchers(pathApiDocs + "/**").permitAll();
//        http.authorizeRequests().antMatchers(GET, "/api/users/**").hasAnyAuthority("ROLE_USER");
//        http.authorizeRequests().antMatchers(POST, "/api/users/**").hasAnyAuthority("ROLE_ADMIN");
        // установить доступ только для аутентифицированных пользователей на все запросы (кроме описанных выше)
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        // добавить фильтр перед всеми запросами
        http.addFilterBefore(new CustomAuthorizationFilter(this.secretTokenKey), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

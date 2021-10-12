package org.example.config.security;

import org.example.config.handler.SuccessUserHandler;
import org.example.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserServiceImpl userServiceImpl;

    @Autowired
    public UserDetailsService userDetailsService() {
        return userServiceImpl;
    }

    private UserDetailsService userDetailsService;// сервис, с помощью которого тащим пользователя

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SecurityConfig(@Lazy@Qualifier("userServiceImpl") UserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder); // конфигурация для прохождения аутентификации
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests();

        http.formLogin()
                .successHandler(new SuccessUserHandler())
                .permitAll();

        http.logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .and().csrf().disable();

        http
                .authorizeRequests()
                .antMatchers("/admin/**").access("hasAnyRole('ADMIN', 'USER')")
                .anyRequest().authenticated();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

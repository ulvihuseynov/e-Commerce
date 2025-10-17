package org.ideaprojects.ecommerce.security;

import org.ideaprojects.ecommerce.security.jwt.AuthTokenFilter;
import org.ideaprojects.ecommerce.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthTokenFilter authTokenFilter;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthenticationEntryPoint authenticationEntryPoint, AuthTokenFilter authTokenFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

       return http.csrf(AbstractHttpConfigurer::disable)
               .exceptionHandling(exception->
                       exception.authenticationEntryPoint(authenticationEntryPoint))
               .sessionManagement(session->
                       session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authorizeHttpRequests(
                auth->auth.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated()
               )
               .authenticationProvider(authenticationProvider())
               .addFilterBefore(authTokenFilter,
                       UsernamePasswordAuthenticationFilter.class)
               .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (
                web->web.ignoring().requestMatchers(
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                )
                );
    }
}

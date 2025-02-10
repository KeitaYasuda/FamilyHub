package com.familyhub.usermanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // CSRF無効化
          .csrf(csrf -> csrf.disable())
          // セッション管理をステートレスに設定
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          // リクエスト認可の設定
          .authorizeHttpRequests(authz -> authz
              .requestMatchers("/auth/**").permitAll() // /auth/** は認証不要
              .requestMatchers(HttpMethod.GET, "/public/**").permitAll() // GET /public/** は認証不要
              .anyRequest().authenticated()
          )
          // JWT認証フィルターを UsernamePasswordAuthenticationFilter の前に追加
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
          
        return http.build();
    }
}

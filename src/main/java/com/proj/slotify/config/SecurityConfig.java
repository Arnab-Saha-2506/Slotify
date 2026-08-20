package com.proj.slotify.config;

import com.proj.slotify.security.JwtAuthEntryPoint;
import com.proj.slotify.security.JwtAuthFilter;
import com.proj.slotify.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final JwtAuthEntryPoint jwtAuthEntryPoint;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

        public SecurityConfig(
                JwtAuthFilter jwtAuthFilter,
                JwtAuthEntryPoint jwtAuthEntryPoint,
                OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
                this.jwtAuthFilter = jwtAuthFilter;
                this.jwtAuthEntryPoint = jwtAuthEntryPoint;
                this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // .cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/health",
                                                                "/ping",
                                                                "/webjars/**",
                                                                "/api/v1/auth/register",
                                                                "/api/v1/auth/login",
                                                                "/api/v1/auth/logout",
                                                                "/api/v1/auth/google",
                                                                "/oauth2/authorization/google",
                                                                "/login/oauth2/code/**",
                                                                "/api/v1/users/*/slots")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/bookings").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthEntryPoint))
                        .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2AuthenticationSuccessHandler))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                // .httpBasic(httpBasic -> httpBasic.disable()) // remove the login popup
                // .formLogin(formLogin -> formLogin.disable()) // no login page redirect
                // .csrf(csrf -> csrf.disable()); // API doesn't need CSRF

                return http.build();
        }
}

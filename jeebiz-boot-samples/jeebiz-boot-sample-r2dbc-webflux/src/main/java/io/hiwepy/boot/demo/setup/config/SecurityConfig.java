package io.hiwepy.boot.demo.setup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange()
                .pathMatchers("/monitor/**").hasRole("ADMIN")
                .anyExchange().permitAll()
                .and().cors()
                .and().httpBasic()
                .and().csrf().disable();
        return http.build();
    }
}

package com.wade.springsecuritydemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 設定 session 的創建機制
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                // 設定 CSRF 保護
//                .csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(createCsrfHandler())
                        .ignoringRequestMatchers("/register", "/userLogin") // 忽略 csrf 保護的 api
                )
                // 設定 CORS 跨域
                .cors(cors -> cors
                        .configurationSource(createCorsConfig())
                )
                // 添加自定義 filter
                .addFilterBefore(new MyFilter(), BasicAuthenticationFilter.class)
                // 設定 Http Basic 認證與表單認證
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                // API 存取權限控制
                .authorizeHttpRequests(request -> request
                        // register
                        .requestMatchers("/register").permitAll()
                        // user login
                        .requestMatchers("/userLogin").authenticated()
                        // movie
                        .requestMatchers("/getMovies").hasAnyRole("NORMAL_MEMBER", "MOVIE_MANAGER", "ADMIN")
                        .requestMatchers("/watchFreeMovie").hasAnyRole("NORMAL_MEMBER", "ADMIN")
                        .requestMatchers("/watchVipMovie").hasAnyRole("VIP_MEMBER", "ADMIN")
                        .requestMatchers("/uploadMovie").hasAnyRole("MOVIE_MANAGER", "ADMIN")
                        .requestMatchers("/deleteMovie").hasAnyRole("MOVIE_MANAGER", "ADMIN")
                        // subscription
                        .requestMatchers("/subscribe", "/unsubscribe").hasAnyRole("NORMAL_MEMBER", "ADMIN")

                        .anyRequest().denyAll()
                )
                .build();
    }

    private CsrfTokenRequestAttributeHandler createCsrfHandler() {
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        return csrfHandler;
    }

    // cors
    private CorsConfigurationSource createCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://example.com")); // 允許的domain
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

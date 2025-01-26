package com.project.smunionbe.global.config;

import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.global.config.jwt.TokenAuthenticationFilter;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    //인증이 필요하지 않은 url
    private final String[] allowedUrls = {
            "/v3/api-docs/**",    // Allow access to OpenAPI docs
            "/swagger-ui/**",     // Allow access to Swagger UI
            "/swagger-ui.html" ,
            "/swagger-resources/**",
            "/api/v1/email/send/signup",
            "/api/v1/email/verify",
            "/api/v1/users/signup", //회원가입은 인증이 필요하지 않음
            "/api/v1/users/login", //로그인은 인증이 필요하지 않음
            "/api/v1/users/refresh", //accessToken 재발급은 인증이 필요하지 않음
    };

    @Bean //인증 관리자 관련 설정
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); //사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean //패스워드 인코더로 사용할 빈 등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberRepository memberRepository) throws Exception {

        // CORS 정책 설정
        http
                .cors(cors -> cors
                        .configurationSource(CorsConfig.apiConfigurationSource()));

        // csrf 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable);

        // form 로그인 방식 비활성화 -> REST API 로그인을 사용할 것이기 때문에
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 비활성화
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // 세션을 사용하지 않음. (세션 생성 정책을 Stateless 설정.)
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 인가
        http
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 요청 및 인증 필요 없는 URL 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(allowedUrls).permitAll()
                        // 인증 없이 허용되는 GET 요청
                        .requestMatchers(HttpMethod.GET, "/api/v1/community").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/{articleId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/{articleId}/replies").permitAll()

                        // 인증이 필요한 요청 (POST, PATCH, GET /likes 포함)
                        .requestMatchers(HttpMethod.POST, "/api/v1/community").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/{articleId}/likes").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/community/{articleId}/likes").authenticated()

                        // 그 외 모든 `/api/v1/community/**` 경로 인증 필요
                        .requestMatchers("/api/v1/community/**").authenticated()

                        .anyRequest().authenticated()); // 그 외 모든 요청은 인증 필요

        http
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}

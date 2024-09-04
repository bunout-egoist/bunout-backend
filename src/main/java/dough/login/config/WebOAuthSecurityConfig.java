package dough.login.config;


import dough.login.config.jwt.TokenAuthenticationFilter;
import dough.login.config.jwt.TokenProvider;
import dough.login.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import dough.login.config.oauth.OAuth2SuccessHandler;
import dough.login.service.CustomOAuth2UserService;
import dough.login.domain.repository.RefreshTokenRepository;
import dough.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final CustomOAuth2UserService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/img/**", "/css/**", "/static/js/**", "/docs/**").permitAll()
                        .requestMatchers("/api/v1/auth/login/kakao").permitAll()
                        .requestMatchers("/api/v1/auth/login/apple").permitAll()
                        .requestMatchers("/api/v1/auth/**", "/oauth2/**").permitAll()
                        .requestMatchers("/api/v1/token", "/api/v1/refreshToken").permitAll()
                        .requestMatchers("https://3a28-210-110-128-18.ngrok-free.app/api/v1/auth/login/apple").permitAll()
                        .requestMatchers("https://3a28-210-110-128-18.ngrok-free.app/api/v1/auth/login/apple/callback").permitAll()
                        .requestMatchers("/api/v1/auth/login/apple").permitAll()
                        .requestMatchers("/api/v1/auth/login/apple/callback").permitAll()
                        .requestMatchers("/api/v1/logout").permitAll()
                        .requestMatchers("/api/v1/quests").permitAll()
                        .requestMatchers("/api/v1/signout").permitAll()
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        .successHandler(oAuth2SuccessHandler())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                loginService
        );
    }


    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }


    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://appleid.apple.com/appleauth/auth/oauth/authorize", "https://3a28-210-110-128-18.ngrok-free.app", "http://13.124.151.164:8080")
                        .allowedMethods("GET","POST","PUT","DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
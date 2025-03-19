
package com.ecommerce.Ecommerce.config;

import com.ecommerce.Ecommerce.service.UserService;

import com.ecommerce.Ecommerce.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityConfig(UserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // .requestMatchers("/user/**").authenticated() // Yêu cầu xác thực cho các
                        // endpoint người dùng
                        .anyRequest().permitAll() // Cho phép truy cập các endpoint khác
                )
                .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu không cần
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            // Lấy thông tin người dùng từ OAuth2
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            OAuth2User oauthUser = oauthToken.getPrincipal();

                            String email = oauthUser.getAttribute("email");
                            String name = oauthUser.getAttribute("name");
                            String googleId = oauthUser.getAttribute("sub");
                            Boolean emailVerified = oauthUser.getAttribute("email_verified");

                            // Lấy AccessToken từ OAuth2AuthorizedClientService
                            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                                    oauthToken.getAuthorizedClientRegistrationId(),
                                    oauthToken.getName());
                            String accessToken = client.getAccessToken().getTokenValue();

                            // Lưu thông tin người dùng vào CSDL nếu chưa tồn tại
                            Optional<User> existingUser = userService.getUserByGoogleId(googleId);
                            User user;
                            if (existingUser.isEmpty()) {
                                user = userService.createUserFromGoogle(email, name, googleId, null, accessToken,
                                        emailVerified);
                            } else {
                                user = existingUser.get();
                            }

                            // Lưu thông tin người dùng vào session
                            request.getSession().setAttribute("user", user);

                            // Chuyển hướng về client
                            response.sendRedirect("http://localhost:3000/");
                        }));
        return http.build();
    }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Chỉ định rõ origin
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // Thêm PATCH
    configuration.setAllowedHeaders(List.of("*")); // Cho phép tất cả các header
    configuration.setAllowCredentials(true); // Cho phép gửi thông tin xác thực
    // Thêm các headers phản hồi
    configuration.addExposedHeader("Access-Control-Allow-Origin");
    configuration.addExposedHeader("Access-Control-Allow-Credentials");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}

}

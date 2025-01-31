package com.nomcci.user.management.config;
import com.nomcci.user.management.util.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${wallet.jwks.url}")
    private String walletJwks;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/.well-known/jwks.json").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/internal/wallet/**").hasAuthority("SCOPE_WALLET_ACCESS")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .cors(Customizer.withDefaults());
        return http.build();
    }


    // Configuración de JwtDecoder para manejar JWKS y llave pública local
    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            // Decodifica el token (String) directamente y extrae los claims
            String subject = getClaimFromToken(token, "sub");
            String audience = getClaimFromToken(token, "aud");

            if ("service-token".equals(subject) && "wallet-service".equals(audience)) {
                System.out.println("Entro wallet service "+ token);
                return NimbusJwtDecoder.withJwkSetUri(walletJwks).build().decode(token);
            } else {
                System.out.println("Entro auth service "+ token);
                return NimbusJwtDecoder.withPublicKey(RsaKeyUtil.loadRSAPublicKey()).build().decode(token);
            }
        };
    }

    private String getClaimFromToken(String token, String claim) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT token");
            }

            // Decodifica la parte del payload
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);

            // Retorna el valor del claim solicitado
            return jsonNode.has(claim) ? jsonNode.get(claim).asText() : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse claim from JWT", e);
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

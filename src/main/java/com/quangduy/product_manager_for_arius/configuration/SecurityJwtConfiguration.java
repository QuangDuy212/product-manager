package com.quangduy.product_manager_for_arius.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.service.InvalidatedTokenService;
import com.quangduy.product_manager_for_arius.util.SecurityUtil;

@Configuration
public class SecurityJwtConfiguration {

    @Value("${quangduy.jwt.base64-secret-access}")
    private String accessTokenKey;

    @Value("${quangduy.jwt.base64-secret-fresh}")
    private String refreshTokenKey;

    @Autowired
    private InvalidatedTokenService invalidatedTokenService;

    private SecretKey getSecretKey(String base64Key) {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // This example uses accessTokenKey for encoding. Update as necessary.
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(accessTokenKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Create decoders for access and refresh tokens
        NimbusJwtDecoder accessTokenDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(accessTokenKey))
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

        NimbusJwtDecoder refreshTokenDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(refreshTokenKey))
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

        return token -> {
            try {
                if (invalidatedTokenService.checkToken(token)) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                // Parse the token and determine the type (access or refresh)
                Jwt jwt = accessTokenDecoder.decode(token);
                if ("refresh".equals(jwt.getClaims().get("token_type"))) {
                    // Decode as refresh token
                    jwt = refreshTokenDecoder.decode(token);
                }
                return jwt;
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}

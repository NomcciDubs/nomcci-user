package com.nomcci.user.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${issuer.url}")
    private String issuerUrl;

    public JwtUtil() {
        RsaKeyUtil.generateKeyPair(); // Genera las claves si no existen
        this.privateKey = RsaKeyUtil.loadPrivateKey();
        this.publicKey = RsaKeyUtil.loadPublicKey();
    }

    /**
     * Genera un token JWT para el usuario.
     *
     * @param userId ID del usuario.
     * @return Token JWT.
     */
    public String generateToken(Long userId) {
        try {
            long expirationTime = 3600000;
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .setIssuedAt(now)
                    .setExpiration(expirationDate)
                    .setIssuer(issuerUrl)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }

}

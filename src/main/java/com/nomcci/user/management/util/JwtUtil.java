package com.nomcci.user.management.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

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
            long expirationTime = 3600000; // 1 hora en milisegundos
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(String.valueOf(userId)) // Usar solo el ID como subject
                    .setIssuedAt(now)
                    .setExpiration(expirationDate)
                    .setIssuer("http://localhost")
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }



    /**
     * Valida el token JWT y obtiene los claims.
     *
     * @param token Token JWT a validar.
     * @return Claims (informacion dentro del token).
     */
    public Claims validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Token JWT invalido o mal formado", e);
        }
    }

}

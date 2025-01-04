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
     * @param username Nombre de usuario para el token.
     * @return Token JWT.
     */
    public String generateToken(String username, String role) {
        try {
            long expirationTime = 3600000; // 1 hora en milisegundos
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(now)
                    .setExpiration(expirationDate)
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

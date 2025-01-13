package com.nomcci.user.management.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JWKSUtil {

    private static final Logger logger = Logger.getLogger(JWKSUtil.class.getName());

    // Ruta del archivo de la llave pública
    private static final String PUBLIC_KEY_FILE = "path/to/public-key.pem";

    /**
     * Genera el JWKS en formato JSON a partir de la llave pública.
     *
     * @return Un mapa representando el JWKS.
     */
    public static Map<String, Object> generateJWKS() {
        try {
            PublicKey publicKey = RsaKeyUtil.loadPublicKey(); // Carga la llave pública
            String kid = generateKID(publicKey); // Genera el KID dinámico basado en el hash de la llave pública
            String modulus = getModulus(publicKey);
            String exponent = getExponent(publicKey);

            // Construcción del JWKS
            Map<String, String> key = new HashMap<>();
            key.put("kty", "RSA");
            key.put("kid", kid);
            key.put("use", "sig");
            key.put("alg", "RS256");
            key.put("n", modulus);
            key.put("e", exponent);

            return Collections.singletonMap("keys", Collections.singletonList(key));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al generar el JWKS", e);
            throw new RuntimeException("Error al generar el JWKS", e);
        }
    }

    /**
     * Genera un KID dinámico a partir del hash de la llave pública.
     *
     * @param publicKey La llave pública.
     * @return El KID generado.
     */
    private static String generateKID(PublicKey publicKey) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(publicKey.getEncoded());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al generar el KID", e);
            throw new RuntimeException("Error al generar el KID", e);
        }
    }

    /**
     * Obtiene el módulo (modulus) de la llave pública en formato Base64 URL seguro.
     *
     * @param publicKey La llave pública.
     * @return El módulo codificado.
     */
    private static String getModulus(PublicKey publicKey) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            java.security.interfaces.RSAPublicKey rsaPublicKey =
                    (java.security.interfaces.RSAPublicKey) keyFactory.generatePublic(spec);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getModulus().toByteArray());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener el módulo de la llave pública", e);
            throw new RuntimeException("Error al obtener el módulo de la llave pública", e);
        }
    }

    /**
     * Obtiene el exponente de la llave pública en formato Base64 URL seguro.
     *
     * @param publicKey La llave pública.
     * @return El exponente codificado.
     */
    private static String getExponent(PublicKey publicKey) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            java.security.interfaces.RSAPublicKey rsaPublicKey =
                    (java.security.interfaces.RSAPublicKey) keyFactory.generatePublic(spec);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getPublicExponent().toByteArray());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener el exponente de la llave pública", e);
            throw new RuntimeException("Error al obtener el exponente de la llave pública", e);
        }
    }
}
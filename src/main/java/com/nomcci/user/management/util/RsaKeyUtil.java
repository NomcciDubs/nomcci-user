package com.nomcci.user.management.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RsaKeyUtil {

    private static final String PRIVATE_KEY_FILE = "private_key.pem";
    private static final String PUBLIC_KEY_FILE = "public_key.pem";
    private static final Logger logger = Logger.getLogger(RsaKeyUtil.class.getName());

    /**
     * Genera un nuevo par de claves RSA si no existen y las guarda en archivos.
     */
    public static void generateKeyPair() {
        try {
            if (!keysExist()) {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair pair = keyGen.generateKeyPair();

                saveKeyToFile(pair.getPrivate(), PRIVATE_KEY_FILE);
                saveKeyToFile(pair.getPublic(), PUBLIC_KEY_FILE);

                logger.info("Par de claves RSA generado y guardado.");

                verifyKeyPair();
            } else {
                logger.info("Las claves RSA ya existen.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al generar las claves RSA", e);
            throw new RuntimeException("Error al generar las claves RSA", e);
        }
    }

    /**
     * Verifica que la clave privada y la clave pública sean válidas.
     */
    public static void verifyKeyPair() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            PublicKey publicKey = loadPublicKey();

            String testMessage = "Mensaje de prueba";
            byte[] signature = signMessage(privateKey, testMessage);
            boolean isValid = verifySignature(publicKey, testMessage, signature);

            if (isValid) {
                logger.info("La clave privada y la clave pública son válidas y coinciden.");
                System.out.println("La clave privada y la clave pública son válidas y coinciden.");
            } else {
                logger.severe("La clave privada y la clave pública no coinciden.");
                System.err.println("La clave privada y la clave pública no coinciden.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al verificar las claves", e);
        }
    }

    /**
     * Firma un mensaje utilizando la clave privada.
     */
    private static byte[] signMessage(PrivateKey privateKey, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }

    /**
     * Verifica una firma utilizando la clave pública.
     */
    private static boolean verifySignature(PublicKey publicKey, String message, byte[] signature) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(message.getBytes());
        return sig.verify(signature);
    }

    /**
     * Carga la clave privada desde el archivo.
     */
    public static PrivateKey loadPrivateKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(PRIVATE_KEY_FILE).toPath());
            String keyString = new String(keyBytes)
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decodedKey = Base64.getDecoder().decode(keyString);

            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(decodedKey));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar la clave privada", e);
            throw new RuntimeException("Error al cargar la clave privada", e);
        }
    }

    /**
     * Carga la clave pública desde el archivo.
     */
    public static PublicKey loadPublicKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(PUBLIC_KEY_FILE).toPath());
            String keyString = new String(keyBytes)
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decodedKey = Base64.getDecoder().decode(keyString);

            return KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(decodedKey));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar la clave pública", e);
            throw new RuntimeException("Error al cargar la clave pública", e);
        }
    }

    public static RSAPublicKey loadRSAPublicKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(PUBLIC_KEY_FILE).toPath());
            String keyString = new String(keyBytes)
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decodedKey = Base64.getDecoder().decode(keyString);

            PublicKey publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(decodedKey));

            // Verifica si la clave es una instancia de RSAPublicKey
            if (!(publicKey instanceof RSAPublicKey)) {
                throw new IllegalArgumentException("La clave pública no es una instancia de RSAPublicKey");
            }

            return (RSAPublicKey) publicKey;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar la clave pública", e);
            throw new RuntimeException("Error al cargar la clave pública", e);
        }
    }

    private static boolean keysExist() {
        return new File(PRIVATE_KEY_FILE).exists() && new File(PUBLIC_KEY_FILE).exists();
    }

    private static void saveKeyToFile(Key key, String fileName) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        StringBuilder formattedKey = new StringBuilder();

        // Encabezado
        if (key instanceof PrivateKey) {
            formattedKey.append("-----BEGIN PRIVATE KEY-----\n");
        } else {
            formattedKey.append("-----BEGIN PUBLIC KEY-----\n");
        }

        // Divide la clave en líneas de 64 caracteres
        int index = 0;
        while (index < encodedKey.length()) {
            int endIndex = Math.min(index + 64, encodedKey.length());
            formattedKey.append(encodedKey, index, endIndex).append("\n");
            index += 64;
        }

        // Pie de la clave
        if (key instanceof PrivateKey) {
            formattedKey.append("-----END PRIVATE KEY-----\n");
        } else {
            formattedKey.append("-----END PUBLIC KEY-----\n");
        }

        // Guarda en el archivo
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(formattedKey.toString());
        }
    }
}

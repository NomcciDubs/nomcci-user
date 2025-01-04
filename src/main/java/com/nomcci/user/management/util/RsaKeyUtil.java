package com.nomcci.user.management.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.util.Base64;

public class RsaKeyUtil {

    private static final String PRIVATE_KEY_FILE = "private_key.pem";
    private static final String PUBLIC_KEY_FILE = "public_key.pem";

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

                System.out.println("Par de claves RSA generado y guardado.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar las claves RSA", e);
        }
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
            throw new RuntimeException("Error al cargar la clave publica", e);
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

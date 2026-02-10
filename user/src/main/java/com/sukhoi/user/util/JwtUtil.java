package com.sukhoi.user.util;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Service
public class JwtUtil {

    private RSAPrivateKey privateKey;

    @Value("${private-key.path}")
    private String privateKeyPath;

    @PostConstruct
    public void readPKCS8PrivateKey() throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\\s", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public String generateToken(int id, long expirationMillis) {
        return Jwts.builder()
                .signWith(privateKey)
                .claim("id", id)
                .issuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .compact();
    }

    public String generateRefreshToken(int id) {
        return generateToken(id, 604800000);
    }

    public String generateAccessToken(int id) {
        return generateToken(id, 3600000);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token)
                .getPayload().get("id").toString();
    }

    private java.security.PublicKey getPublicKey() {
        try {
            if (privateKey instanceof java.security.interfaces.RSAPrivateCrtKey) {
                java.security.interfaces.RSAPrivateCrtKey crtKey = (java.security.interfaces.RSAPrivateCrtKey) privateKey;
                java.security.spec.RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(
                        crtKey.getModulus(),
                        crtKey.getPublicExponent());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(publicKeySpec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

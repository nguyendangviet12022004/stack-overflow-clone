package com.sukhoi.user.util;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
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

    @PostConstruct
    public void readPKCS8PrivateKey() throws Exception {
        String userDir = System.getProperty("user.dir");
        String key = new String(Files.readAllBytes(Paths.get(userDir,"user/src/main/resources/private.pem")));

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\\s", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        this.privateKey =  (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public String generateToken(int id, long expirationMillis) {
        return Jwts.builder()
                .signWith(privateKey)
                .claim("id",id)
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


}

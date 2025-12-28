package com.sukhoi.gateway.util;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

@Service
public class JwtUtil {

    private RSAPublicKey publicKey;

    @PostConstruct
    public void readPKCS8PublicKey() throws Exception {
        String userDir = System.getProperty("user.dir");
        String key = new String(Files.readAllBytes(Paths.get(userDir,"gateway/src/main/resources/public.pem")));

        String privateKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public int validateToken(String token) {
        try {
            int id = Integer.parseInt(Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token).getPayload().get("id").toString());
            return id;
        } catch (Exception e) {
            return 0;
        }
    }


}

package com.eshop.api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        // Prefer Base64 secrets. If not Base64, fall back to raw UTF-8 bytes.
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ignore) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // Enforce >= 256 bits for HS256
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret must be at least 256 bits (32 bytes). " +
                            "Provide a Base64-encoded 32-byte value."
            );
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        //this.key = Keys.hmacShaKeyFor(secret.getBytes());
        //this.expirationMs = expirationMs;
    }

    /** Generate a JWT containing username as subject and expiry date */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extract username (subject) from token */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Validate token (checks signature and expiration) */
    public boolean validateToken(String token) {
        try {
            //Jwts.parserBuilder()
            //        .setSigningKey(key)
             //       .build()
            //        .parseClaimsJws(token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // log or handle invalid/expired token
            return false;
        }
    }
}

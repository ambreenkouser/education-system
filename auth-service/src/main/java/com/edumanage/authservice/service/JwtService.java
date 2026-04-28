package com.edumanage.authservice.service;

import com.edumanage.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    // No hardcoded default — fail fast on startup if JWT_SECRET env var is missing
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms:900000}")
    private long expirationMs;

    private final StringRedisTemplate redisTemplate;

    private static final String REVOKED_KEY_PREFIX = "revoked:";

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("email", user.getEmail());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getId().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshTokenValue() {
        return UUID.randomUUID().toString();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(new Date())) return false;
            // Reject tokens that have been explicitly revoked
            String jti = claims.getId();
            return !Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_KEY_PREFIX + jti));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Revokes a token by storing its JTI in Redis until its natural expiry time.
     * The api-gateway reads the same Redis key to block revoked tokens at the edge.
     */
    public void revokeToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String jti = claims.getId();
            long ttlMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttlMs > 0) {
                redisTemplate.opsForValue().set(REVOKED_KEY_PREFIX + jti, "1", Duration.ofMillis(ttlMs));
            }
        } catch (Exception ignored) {
            // Token already expired — no revocation needed
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

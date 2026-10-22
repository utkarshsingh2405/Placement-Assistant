package com.placementassist.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for issuing and validating JWT access and refresh tokens across Portal,
 * Institute, and Placement pillars. Standardizes claims: sub, role, uid, rollNo, name.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(String subject, Long userId, Role role, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>();
        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }
        claims.put("userId", userId);
        claims.put("uid", userId);
        claims.put("role", role.name());
        return buildToken(subject, claims, accessExpirationMs);
    }

    public String generatePortalToken(String subject, Long uid, Role role, String rollNo, String name, Long companyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", uid);
        claims.put("userId", uid);
        claims.put("role", role.name());
        if (rollNo != null) claims.put("rollNo", rollNo);
        if (name != null) claims.put("name", name);
        if (companyId != null) claims.put("companyId", companyId);
        return buildToken(subject, claims, accessExpirationMs);
    }

    public String generateRefreshToken(String subject, Long userId) {
        return Jwts.builder()
                .subject(subject)
                .claim("userId", userId)
                .claim("uid", userId)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    private String buildToken(String subject, Map<String, Object> claims, long expiration) {
        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration));

        claims.forEach(builder::claim);
        return builder.signWith(signingKey).compact();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object uidObj = claims.get("uid");
        if (uidObj instanceof Number num) {
            return num.longValue();
        }
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Number num) {
            return num.longValue();
        }
        return null;
    }

    public Long extractUid(String token) {
        return extractUserId(token);
    }

    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleStr = (String) claims.get("role");
        return roleStr != null ? Role.valueOf(roleStr) : null;
    }

    public String extractRollNo(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("rollNo");
    }

    public String extractName(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("name");
    }

    public Long extractCompanyId(String token) {
        Claims claims = extractAllClaims(token);
        Object comp = claims.get("companyId");
        if (comp instanceof Number num) {
            return num.longValue();
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String token) {
        return extractAllClaims(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

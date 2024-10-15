package org.example.configuration.jwt

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import java.util.function.Function

@Component
class JwtTokenUtil {

    @Value('${jwt.secret}')
    private String secretKey

    String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = [:]
        return createToken(claims, userDetails.username)
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()
    }

    Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject)
    }

    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration)
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date())
    }
}

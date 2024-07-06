package com.example.commonsecurity.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.secret-key}")
    String secretKey;

    @Value("${application.security.access-token-expiration}")
    Long accessTokenExpiration;

    @Value("${application.security.refresh-token-expiration}")
    Long refreshTokenExpiration;

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public List<String> extractRoles(String token){
        Claims claims = extractAllClaims(token);
        List<String> roles = (List<String>) claims.get("roles");

        return roles != null ? roles : Collections.emptyList();
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public String generateToken(
            Map<String,Object> extractClaims,
            UserDetails userDetails,
            long tokenExpiration
    ){
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes= Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        List<String> userRoles = extractRoles(userDetails);

        Map<String, Object> extractClaims = new HashMap<>();
        extractClaims.put("roles", userRoles);

        return generateToken(extractClaims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails){
        List<String> userRoles = extractRoles(userDetails);

        Map<String, Object> extractClaims = new HashMap<>();
        extractClaims.put("roles", userRoles);

        return generateToken(extractClaims,userDetails,refreshTokenExpiration);
    }

    private static List<String> extractRoles(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return userRoles;
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUsername(token);
        log.info("Token username -> {}",username);
        log.info("Username -> {}",userDetails.getUsername());
        log.info("Token expired ->{}",isTokenExpired(token));

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}

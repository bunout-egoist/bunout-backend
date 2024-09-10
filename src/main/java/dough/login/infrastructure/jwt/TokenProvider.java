package dough.login.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
public class TokenProvider {

    public static final String EMPTY_SUBJECT = "";
    private final String issuer;
    private final Key secretKey;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public TokenProvider(
            @Value("${jwt.issuer}") final String issuer,
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access-expiration-time}") final Long accessExpirationTime,
            @Value("${jwt.refresh-expiration-time}") final Long refreshExpirationTime
    ) {
        this.issuer = issuer;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public String generateAccessToken(final String subject) {
        final String accessToken = createToken(subject, accessExpirationTime);
        return accessToken;
    }

    public String generateRefreshToken() {
        final String refreshToken = createToken(EMPTY_SUBJECT, refreshExpirationTime);
        return refreshToken;
    }

    private String createToken(final String subject, final Long expiryInMilliseconds) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + expiryInMilliseconds);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(subject)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Authentication getAuthentication(final String token) {
        final String subject = getSubject(token);
        final Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new User(subject, "", authorities), token, authorities);
    }

    public String getSubject(final String token) {
        return getClaims(token)
                .getBody()
                .getSubject();
    }

    private Jws<Claims> getClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
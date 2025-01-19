package com.aydnorcn.mis_app.jwt;

import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.utils.MessageConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.key}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationDate;


    //Generate token from authentication
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username) // Set claims
                .issuedAt(new Date()) // Set created date
                .expiration(expireDate) // Set expiration date
                .signWith(key()) // Set signature Key
                .compact(); // Build token
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //get username from token
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    //validate jwt token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException ex) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException ex) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.JWT_CLAIMS_EMPTY);
        }
    }
}

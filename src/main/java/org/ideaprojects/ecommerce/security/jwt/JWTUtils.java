package org.ideaprojects.ecommerce.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtils {

    private static final Logger logger= LoggerFactory.getLogger(JWTUtils.class);

    @Value("${spring.app.jwtExpirationMS}")
    private Long jwtExpirationMS;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header:  {}",bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
           return bearerToken.substring(7);
        }
        return null;
    }

    public String generateTokenFromUserName(UserDetails userDetails){

        String username = userDetails.getUsername();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime()+jwtExpirationMS)))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJWTToken(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(jwtSecret)
        );
    }

    public boolean validateJWTToken(String authToken){

        try{
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }catch (MalformedJwtException e){
             logger.error("invalid jwt token: {}",e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error(" jwt token is expired : {}",e.getMessage());

        }catch (UnsupportedJwtException e){
            logger.error(" jwt token is unsupported : {}",e.getMessage());

        }catch (IllegalArgumentException e){
            logger.error(" jwt claims string  is empty: {}",e.getMessage());


        }
        return false;
    }
}

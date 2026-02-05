package com.library.loansystem.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt-key}")
    private String secretKey;

    @Value("${security.jwt.issuer}")
    private String userGenerator;


    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return buildToken(username, authorities, algorithm);
    }

    public String createTokenFromEntity(UserEntity user){
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        String authorities = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.joining(","));

        return buildToken(user.getUsername(), authorities, algorithm);
    }

    public String buildToken(String username, String authorities, Algorithm algorithm){
        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);
        }catch (TokenExpiredException exception) {
            throw new JWTVerificationException("Token has expired");
        } catch (SignatureVerificationException exception) {
            throw new JWTVerificationException("Token signature is invalid (Tampering detected)");
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token is invalid: " + exception.getMessage());
        }
    }

    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}

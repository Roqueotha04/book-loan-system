package com.library.loansystem.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secretKey = "my_very_secret_key_for_testing_purposes";
    private final String issuer = "test_issuer";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtUtils, "userGenerator", issuer);
    }

    @Test
    void testCreateToken() {
        Authentication auth = mock(Authentication.class);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(auth.getName()).thenReturn("testUser");
        doReturn(authorities).when(auth).getAuthorities();

        String token = jwtUtils.createToken(auth);

        assertNotNull(token);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        assertEquals("testUser", decodedJWT.getSubject());
        assertEquals("ROLE_USER", decodedJWT.getClaim("authorities").asString());
    }

    @Test
    void testCreateAndValidateToken() {
        String username = "testUser";
        String authorities = "ROLE_USER,ROLE_ADMIN";
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token = jwtUtils.buildToken(username, authorities, algorithm);

        assertNotNull(token);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        assertEquals(username, jwtUtils.extractUsername(decodedJWT));
        assertEquals(issuer, decodedJWT.getIssuer());
        assertEquals(authorities, jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString());
    }

    @Test
    void testCreateTokenFromEntity() {
        UserEntity user = new UserEntity();
        user.setUsername("entityUser");
        Role role = new Role();
        role.setRole("ROLE_USER");
        user.setRoles(Set.of(role));

        String token = jwtUtils.createTokenFromEntity(user);

        assertNotNull(token);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        assertEquals("entityUser", jwtUtils.extractUsername(decodedJWT));
        assertEquals("ROLE_USER", jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString());
    }

    @Test
    void testValidateToken_Expired() {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String expiredToken = JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
                .sign(algorithm);

        assertThrows(JWTVerificationException.class, () -> jwtUtils.validateToken(expiredToken));
    }

    @Test
    void testValidateToken_InvalidSignature() {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer(issuer)
                .sign(algorithm);

        String tamperedToken = token + "modified";

        assertThrows(JWTVerificationException.class, () -> jwtUtils.validateToken(tamperedToken));
    }

    @Test
    void testValidateToken_InvalidGeneral() {
        Algorithm algorithm = Algorithm.HMAC256("my_very_secret_key_for_testing_purposes");
        String token = JWT.create()
                .withIssuer("wrong_issuer")
                .sign(algorithm);

        JWTVerificationException exception = assertThrows(JWTVerificationException.class,
                () -> jwtUtils.validateToken(token));

        assertTrue(exception.getMessage().contains("Token is invalid"));
    }

    @Test
    void testGetAllClaims() {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = jwtUtils.buildToken("user", "ROLE_USER", algorithm);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        Map<String, Claim> claims = jwtUtils.getAllClaims(decodedJWT);

        assertNotNull(claims);
        assertTrue(claims.containsKey("authorities"));
        assertTrue(claims.containsKey("sub"));
    }
}

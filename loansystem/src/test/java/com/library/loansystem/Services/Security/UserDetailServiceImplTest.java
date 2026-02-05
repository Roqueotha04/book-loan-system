package com.library.loansystem.Services.Security;
import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Security.AuthLoginRequest;
import com.library.loansystem.DTO.Security.AuthRegisterRequest;
import com.library.loansystem.DTO.Security.AuthResponse;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.RoleRepository;
import com.library.loansystem.Services.UserEntityService;
import com.library.loansystem.Utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @Mock
    private UserEntityService userEntityService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("test@gmail.com", "testUser", "encodedPass");
        Role role = new Role();
        role.setRole("ROLE_USER");
        userEntity.setRoles(Set.of(role));
    }

    @Test
    void loadUserByUsername_Success() {
        when(userEntityService.findByUsername("testUser")).thenReturn(userEntity);

        UserDetails result = userDetailService.loadUserByUsername("testUser");

        assertNotNull(result);
        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(1, result.getAuthorities().size());
        verify(userEntityService).findByUsername("testUser");
    }

    @Test
    void authenticate_Success() {
        when(userEntityService.findByUsername("testUser")).thenReturn(userEntity);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);

        Authentication result = userDetailService.authenticate("testUser", "rawPass");

        assertNotNull(result);
        assertEquals("testUser", result.getName());
        verify(passwordEncoder).matches("rawPass", "encodedPass");
    }

    @Test
    void authenticate_InvalidPassword() {
        when(userEntityService.findByUsername("testUser")).thenReturn(userEntity);
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userDetailService.authenticate("testUser", "wrongPass"));
    }

    @Test
    void login_Success() {
        AuthLoginRequest loginRequest = new AuthLoginRequest("testUser", "rawPass");
        when(userEntityService.findByUsername("testUser")).thenReturn(userEntity);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("mockToken");

        AuthResponse result = userDetailService.login(loginRequest);

        assertNotNull(result);
        assertEquals("mockToken", result.jwt());
        assertTrue(result.status());
        verify(jwtUtils).createToken(any(Authentication.class));
    }

    @Test
    void createUser_Success() {
        AuthRegisterRequest registerRequest = new AuthRegisterRequest("test@gmail.com", "newUser", "pass123");

        when(userEntityService.findByUsername(registerRequest.username())).thenReturn(userEntity);
        when(passwordEncoder.matches(registerRequest.password(), "encodedPass")).thenReturn(true);
        when(jwtUtils.createToken(any(Authentication.class))).thenReturn("newToken");

        AuthResponse result = userDetailService.createUser(registerRequest);

        assertNotNull(result);
        verify(userEntityService).save(any(UserEntityRequest.class));
        verify(jwtUtils).createToken(any(Authentication.class));
    }
}

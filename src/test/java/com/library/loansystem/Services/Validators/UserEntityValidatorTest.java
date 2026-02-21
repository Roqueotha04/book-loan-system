package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityValidatorTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private GrantedAuthority authorities;

    @InjectMocks
    private UserValidator userValidator;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("test@gmail.com", "testUser", "encodedPass");
        userEntity.setId(1L);
    }

    @Test
    void validateUser_shouldPass() {
        when(userEntityRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(userEntityRepository.existsByUsername("newUser")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUser("new@gmail.com", "newUser"));

        verify(userEntityRepository).existsByEmail("new@gmail.com");
        verify(userEntityRepository).existsByUsername("newUser");
    }

    @Test
    void validateUser_shouldFailIfEmailExists() {
        when(userEntityRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validateUser("existing@gmail.com", "anyUser"));

        assertEquals("Email already in use", ex.getMessage());
        verify(userEntityRepository).existsByEmail("existing@gmail.com");
        verify(userEntityRepository, never()).existsByUsername(anyString());
    }

    @Test
    void validateUser_shouldFailIfUsernameExists() {
        when(userEntityRepository.existsByEmail("ok@gmail.com")).thenReturn(false);
        when(userEntityRepository.existsByUsername("existingUser")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validateUser("ok@gmail.com", "existingUser"));

        assertEquals("Username already in use", ex.getMessage());
        verify(userEntityRepository).existsByEmail("ok@gmail.com");
        verify(userEntityRepository).existsByUsername("existingUser");
    }

    @Test
    void validateUpdateUser_shouldPass() {
        when(userEntityRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(userEntityRepository.existsByUsername("newUser")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUpdateUser(userEntity, "new@gmail.com", "newUser"));
    }

    @Test
    void validateUpdateUser_shouldFailIfEmailConflict() {
        when(userEntityRepository.existsByEmail("conflict@gmail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validateUpdateUser(userEntity, "conflict@gmail.com", "testUser"));

        assertEquals("Email already in use", ex.getMessage());
    }

    @Test
    void validateUpdateUser_shouldFailIfUsernameConflict() {
        when(userEntityRepository.existsByEmail("ok@gmail.com")).thenReturn(false);
        when(userEntityRepository.existsByUsername("conflictUser")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validateUpdateUser(userEntity, "ok@gmail.com", "conflictUser"));

        assertEquals("Username already in use", ex.getMessage());
    }

    @Test
    void validateUserRole_AsAdmin_Success() {
        Long targetUserId = 1L;
        UserEntity expectedUser = new UserEntity();
        expectedUser.setId(targetUserId);

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(userEntityRepository.findById(targetUserId)).thenReturn(Optional.of(expectedUser));

        UserEntity result = userValidator.validateUserRole(targetUserId, authentication);

        assertNotNull(result);
        assertEquals(targetUserId, result.getId());
        verify(userEntityRepository).findById(targetUserId);
        verify(userEntityRepository, never()).findByUsername(anyString());
    }

    @Test
    void validateUserRole_AsLibrarian_Success() {
        Long targetUserId = 2L;
        UserEntity expectedUser = new UserEntity();
        expectedUser.setId(targetUserId);

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_LIBRARIAN"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(userEntityRepository.findById(targetUserId)).thenReturn(Optional.of(expectedUser));

        UserEntity result = userValidator.validateUserRole(targetUserId, authentication);

        assertNotNull(result);
        verify(userEntityRepository).findById(targetUserId);
    }

    @Test
    void validateUserRole_OwnAccount_Success() {
        Long targetUserId = 10L;
        String username = "ownUser";
        UserEntity ownUser = new UserEntity();
        ownUser.setId(targetUserId);
        ownUser.setUsername(username);

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn(username);
        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(ownUser));

        UserEntity result = userValidator.validateUserRole(targetUserId, authentication);

        assertNotNull(result);
        assertEquals(targetUserId, result.getId());
        verify(userEntityRepository).findByUsername(username);
    }

    @Test
    void validateUserRole_OtherAccount_ThrowsAccessDenied() {
        Long targetUserId = 99L;
        Long myActualId = 10L;
        String username = "attackerUser";

        UserEntity myUser = new UserEntity();
        myUser.setId(myActualId);

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn(username);
        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(myUser));

        assertThrows(AccessDeniedException.class,
                () -> userValidator.validateUserRole(targetUserId, authentication));
    }

    @Test
    void validateUserRole_AdminTargetNotFound_ThrowsException() {
        Long targetUserId = 1L;
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        when(userEntityRepository.findById(targetUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userValidator.validateUserRole(targetUserId, authentication));
    }

    @Test
    void validatePassword_shouldPass() {
        when(passwordEncoder.matches("current", "encodedPass")).thenReturn(true);
        when(passwordEncoder.matches("newPass123", "encodedPass")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validatePassword(userEntity, "current", "newPass123"));
    }

    @Test
    void validatePassword_shouldFailIfCurrentIncorrect() {
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validatePassword(userEntity, "wrong", "newPass123"));

        assertEquals("Current password is incorrect", ex.getMessage());
    }

    @Test
    void validatePassword_shouldFailIfSameAsCurrent() {
        when(passwordEncoder.matches("same", "encodedPass")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validatePassword(userEntity, "same", "same"));

        assertEquals("New password must be different from current password", ex.getMessage());
    }

    @Test
    void validatePassword_shouldFailIfTooShort() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validatePassword(userEntity, null, "short"));

        assertEquals("Password must be at least 8 characters long", ex.getMessage());
    }

    @Test
    void validatePassword_shouldFailIfTooWeak() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userValidator.validatePassword(userEntity, null, "testUser"));

        assertEquals("Password is too weak", ex.getMessage());
    }
}


package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void testValidateUser_Ok() {
        // Arrange
        UserRequest request = new UserRequest("nuevo@gmail.com", "nuevoUser", "pass123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validateUser(request));

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByUsername(request.username());
    }

    @Test
    public void testValidateUser_EmailAlreadyExists() {
        // Arrange
        UserRequest request = new UserRequest("repetido@gmail.com", "user", "pass");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userValidator.validateUser(request));

        assertEquals("Email already in use", exception.getMessage());

        // Verificamos que al fallar el email, NO se chequea el username (ahorro de recursos)
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    public void testValidateUser_UsernameAlreadyExists() {
        // Arrange
        UserRequest request = new UserRequest("email@ok.com", "fideo", "pass");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userValidator.validateUser(request));

        assertEquals("Username already in use", exception.getMessage());

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByUsername(request.username());
    }
}

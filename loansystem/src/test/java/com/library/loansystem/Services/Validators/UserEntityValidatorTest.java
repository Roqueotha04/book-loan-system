package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityValidatorTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void testValidateUser_Ok() {
        // Arrange
        UserEntityRequest request = new UserEntityRequest("nuevo@gmail.com", "nuevoUser", "pass123");
        when(userEntityRepository.existsByEmail(request.email())).thenReturn(false);
        when(userEntityRepository.existsByUsername(request.username())).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> userValidator.validateUser(request));

        verify(userEntityRepository).existsByEmail(request.email());
        verify(userEntityRepository).existsByUsername(request.username());
    }

    @Test
    public void testValidateUser_EmailAlreadyExists() {
        // Arrange
        UserEntityRequest request = new UserEntityRequest("repetido@gmail.com", "user", "pass");
        when(userEntityRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userValidator.validateUser(request));

        assertEquals("Email already in use", exception.getMessage());

        // Verificamos que al fallar el email, NO se chequea el username (ahorro de recursos)
        verify(userEntityRepository).existsByEmail(request.email());
        verify(userEntityRepository, never()).existsByUsername(anyString());
    }

    @Test
    public void testValidateUser_UsernameAlreadyExists() {
        // Arrange
        UserEntityRequest request = new UserEntityRequest("email@ok.com", "fideo", "pass");
        when(userEntityRepository.existsByEmail(request.email())).thenReturn(false);
        when(userEntityRepository.existsByUsername(request.username())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userValidator.validateUser(request));

        assertEquals("Username already in use", exception.getMessage());

        verify(userEntityRepository).existsByEmail(request.email());
        verify(userEntityRepository).existsByUsername(request.username());
    }
}

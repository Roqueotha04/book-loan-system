package com.library.loansystem.Services;

import com.auth0.jwt.JWT;
import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Request.UserEntityUpdateRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.DTO.Response.UserEntityUpdateResponse;
import com.library.loansystem.DTO.Security.ResetPasswordRequest;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserEntityMapper;
import com.library.loansystem.Repositories.RoleRepository;
import com.library.loansystem.Repositories.UserEntityRepository;
import static org.junit.jupiter.api.Assertions.*;

import com.library.loansystem.Services.Validators.UserValidator;
import com.library.loansystem.Utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    private UserEntityServiceImpl userService;

    @BeforeEach
    void setUp (){
        UserEntityMapper userEntityMapper = new UserEntityMapper();
        userService = new UserEntityServiceImpl
                (userEntityRepository,
                roleRepository,
                userEntityMapper,
                userValidator,
                passwordEncoder,
                jwtUtils);
    }

    @Test
    public void testFindAll(){
        List<UserEntity> userEntityList = DataProvider.userListMock();

        when(userEntityRepository.findAll()).thenReturn(userEntityList);

        List<UserEntityResponse> result = userService.findAll();

        assertEquals(userEntityList.size(), result.size());
        assertEquals(userEntityList.get(1).getEmail(), result.get(1).email());
        verify(userEntityRepository).findAll();
    }

    @Test
    public void testFindById(){
        UserEntity userEntity = DataProvider.userListMock().get(1);

        when(userEntityRepository.findById(2L)).thenReturn(Optional.of(userEntity));
        UserEntityResponse result = userService.findById(2L);

        assertNotNull(result);
        assertEquals(userEntity.getUsername(), result.username());
        verify(userEntityRepository).findById(2L);
    }

    @Test
    public void testSearchByUsername_ok() {
        String username = "leo";
        List<UserEntity> userEntityList = DataProvider.userListMock();

        when(userEntityRepository.findByUsernameContainingIgnoreCase(username))
                .thenReturn(userEntityList);

        List<UserEntityResponse> result = userService.searchByUsername(username);

        assertNotNull(result);
        assertEquals(userEntityList.size(), result.size());
        assertEquals(userEntityList.get(0).getUsername(), result.get(0).username());

        verify(userEntityRepository).findByUsernameContainingIgnoreCase(username);
    }

    @Test
    public void testFindByEmail_ok() {
        UserEntity userEntity = DataProvider.userListMock().get(0);
        String email = userEntity.getEmail();

        when(userEntityRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));

        UserEntityResponse result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(userEntity.getUsername(), result.username());

        verify(userEntityRepository).findByEmail(email);
    }

    @Test
    public void testFindByEmail_notFound() {
        String email = "noexiste@mail.com";

        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByEmail(email));

        verify(userEntityRepository).findByEmail(email);
    }



    @Test
    public void testSave() {
        UserEntityRequest userEntityRequest = new UserEntityRequest("angeldimaria@gmail.com", "fideo", "dimaria");
        Role defaultRole = new Role();
        defaultRole.setRole("ROLE_USER");
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userEntityRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.save(userEntityRequest);
        assertEquals(userEntityRequest.email(), result.email());
        assertEquals(userEntityRequest.username(), result.username());
        assertNotNull(result.roles());
        assertTrue(result.roles().stream().anyMatch(r -> r.getRole().equals("ROLE_USER")));

        verify(userValidator).validateUser(userEntityRequest.email(), userEntityRequest.username());
        verify(roleRepository).findByRole("ROLE_USER");
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    public void testSave_RoleNotFoundException() {
        UserEntityRequest request = new UserEntityRequest("test@gmail.com", "testUser", "password123");

        doNothing().when(userValidator).validateUser(request.email(), request.username());
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.save(request));

        assertEquals("Error: Role ROLE_USER not found in DB", ex.getMessage());
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    public void testDelete_ok(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));


        userService.deletePermanently(1L);
        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository).delete(any(UserEntity.class));
    }

    @Test
    public void testDelete_hasActiveLoans(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        userEntity.setId(1L);

        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.deletePermanently(1L));

        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository).hasActiveLoans(1L);
        verify(userEntityRepository, never()).delete(any(UserEntity.class));
    }

    @Test
    public void testDeactivate_ok() {
        // Datos de prueba
        UserEntity userEntity = DataProvider.userListMock().get(1);
        Authentication auth = mock(Authentication.class);

        // Mocks
        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(false);
        when(userValidator.validateUserRole(1L, auth)).thenReturn(userEntity);
        when(userEntityRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecución
        UserEntityResponse result = userService.deactivate(1L, auth);

        // Validaciones
        assertFalse(result.active());
        verify(userEntityRepository).hasActiveLoans(1L);
        verify(userValidator).validateUserRole(1L, auth);
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    public void testDeactivate_hasActiveLoans() {
        Authentication auth = mock(Authentication.class);

        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.deactivate(1L, auth));

        verify(userEntityRepository).hasActiveLoans(1L);
        verify(userValidator, never()).validateUserRole(anyLong(), any());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testDeactivate_userAlreadyInactive() {
        Authentication auth = mock(Authentication.class);
        UserEntity userEntity = DataProvider.userListMock().get(1);
        userEntity.setActive(false);

        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(false);
        when(userValidator.validateUserRole(1L, auth)).thenReturn(userEntity);

        assertThrows(BusinessException.class, () -> userService.deactivate(1L, auth));
        verify(userEntityRepository).hasActiveLoans(1L);
        verify(userValidator).validateUserRole(1L, auth);
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testActivate_ok() {
        UserEntity userEntity = DataProvider.userListMock().get(1);
        userEntity.setActive(false);
        Authentication auth = mock(Authentication.class);

        when(userValidator.validateUserRole(1L, auth)).thenReturn(userEntity);
        when(userEntityRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.activate(1L, auth);

        assertTrue(result.active());
        verify(userValidator).validateUserRole(1L, auth);
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    public void testActivate_alreadyActive() {
        UserEntity userEntity = DataProvider.userListMock().get(1);
        Authentication auth = mock(Authentication.class);

        when(userValidator.validateUserRole(1L, auth)).thenReturn(userEntity);

        assertThrows(BusinessException.class, () -> userService.activate(1L, auth));

        verify(userValidator).validateUserRole(1L, auth);
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testUpdate() {
        UserEntity userEntity = DataProvider.userListMock().get(1);
        UserEntityUpdateRequest userEntityRequest = new UserEntityUpdateRequest("messi@gmail.com", "Lionel modified");
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userEntity.getUsername());

        when(userValidator.validateUserRole(1L, auth)).thenReturn(userEntity);
        doNothing().when(userValidator).validateUpdateUser(userEntity, userEntityRequest.email(), userEntityRequest.username());
        when(userEntityRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.createTokenFromEntity(userEntity)).thenReturn("new-token");

        UserEntityUpdateResponse result = userService.update(1L, userEntityRequest, auth);

        assertEquals(userEntityRequest.email(), result.user().email());
        assertEquals(userEntityRequest.username(), result.user().username());
        assertEquals("new-token", result.newToken());

        verify(userValidator).validateUserRole(1L, auth);
        verify(userValidator).validateUpdateUser(userEntity, userEntityRequest.email(), userEntityRequest.username());
        verify(userEntityRepository).save(any(UserEntity.class));
        verify(jwtUtils).createTokenFromEntity(userEntity);
    }


    @Test
    public void testGetUserOrThrow_ok(){
        UserEntity userEntity = DataProvider.userListMock().get(2);

        when(userEntityRepository.findById(2L)).thenReturn(Optional.of(userEntity));
        UserEntity result = userService.getUserOrThrow(2L);
        assertNotNull(result);
        assertEquals(userEntity.getUsername(), result.getUsername());
        verify(userEntityRepository).findById(2L);
    }

    @Test
    public void testGetUserOrThrow_NotFound(){

        when(userEntityRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserOrThrow(2L));
        verify(userEntityRepository).findById(2L);
    }

    @Test
    public void testChangePassword_Success() {
        UserEntity user = DataProvider.userListMock().get(0);
        Long userId = 1L;
        user.setId(userId);

        ResetPasswordRequest request = new ResetPasswordRequest("currentPass123", "newPass123");
        Authentication auth = mock(Authentication.class);

        when(userValidator.validateUserRole(userId, auth)).thenReturn(user);
        doNothing().when(userValidator).validatePassword(user, request.currentPassword(), request.newPassword());
        when(passwordEncoder.encode(request.newPassword())).thenReturn("encodedNewPass");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntityResponse result = userService.changePassword(userId, request, auth);

        assertNotNull(result);
        verify(userValidator).validateUserRole(userId, auth);
        verify(userValidator).validatePassword(user, request.currentPassword(), request.newPassword());
        verify(passwordEncoder).encode(request.newPassword());
        verify(userEntityRepository).save(user);
    }

    @Test
    public void testChangeRoles_Success() {
        Long userId = 1L;
        List<String> rolesNames = List.of("ROLE_ADMIN", "ROLE_USER");
        UserEntity user = DataProvider.userListMock().get(0);
        user.setId(userId);

        Role adminRole = new Role();
        adminRole.setRole("ROLE_ADMIN");
        Role userRole = new Role();
        userRole.setRole("ROLE_USER");

        when(roleRepository.findByRole("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntityResponse result = userService.changeRoles(userId, rolesNames);

        assertNotNull(result);
        assertEquals(2, user.getRoles().size());
        verify(roleRepository).findByRole("ROLE_ADMIN");
        verify(roleRepository).findByRole("ROLE_USER");
        verify(userEntityRepository).save(user);
    }

    @Test
    public void testChangeRoles_RoleNotFound() {
        Long userId = 1L;
        List<String> rolesNames = List.of("ROLE_INVALID");

        when(roleRepository.findByRole("ROLE_INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changeRoles(userId, rolesNames));
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    public void testChangeRoles_EmptyRoles() {
        Long userId = 1L;
        List<String> rolesNames = Collections.emptyList();

        assertThrows(BusinessException.class, () -> userService.changeRoles(userId, rolesNames));
    }

    @Test
    public void testChangeRoles_UserNotFound() {
        Long userId = 99L;
        List<String> rolesNames = List.of("ROLE_USER");
        Role userRole = new Role();
        userRole.setRole("ROLE_USER");

        when(roleRepository.findByRole("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userEntityRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changeRoles(userId, rolesNames));
    }

    @Test
    public void testSearchByUsername_Success() {
        String search = "test";
        List<UserEntity> mockUsers = DataProvider.userListMock();

        when(userEntityRepository.findByUsernameContainingIgnoreCase(search)).thenReturn(mockUsers);

        List<UserEntityResponse> result = userService.searchByUsername(search);

        assertNotNull(result);
        assertEquals(mockUsers.size(), result.size());
        verify(userEntityRepository).findByUsernameContainingIgnoreCase(search);
    }

    @Test
    public void testFindByUsername_Success() {
        String username = "testUser";
        UserEntity user = DataProvider.userListMock().get(0);
        user.setUsername(username);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserEntity result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userEntityRepository).findByUsername(username);
    }

    @Test
    public void testFindByUsername_NotFound() {
        String username = "nonexistent";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.findByUsername(username));

        assertEquals("User not found with username: " + username, ex.getMessage());
        verify(userEntityRepository).findByUsername(username);
    }
}

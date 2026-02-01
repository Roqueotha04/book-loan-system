package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserEntityMapper;
import com.library.loansystem.Repositories.UserEntityRepository;
import static org.junit.jupiter.api.Assertions.*;

import com.library.loansystem.Services.Validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserValidator userValidator;

    private UserEntityServiceImpl userService;

    @BeforeEach
    void setUp (){
        UserEntityMapper userEntityMapper = new UserEntityMapper();
        userService = new UserEntityServiceImpl(userEntityRepository, userEntityMapper, userValidator);
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
    public void testSave (){
        UserEntityRequest userEntityRequest = new UserEntityRequest("angeldimaria@gmail.com", "fideo", "dimaria");
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.save(userEntityRequest);
        assertEquals(userEntityRequest.email(), result.email());
        assertEquals(userEntityRequest.username(), result.username());
        verify(userEntityRepository).save(any(UserEntity.class));
        verify(userValidator).validateUser(userEntityRequest);
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
    public void testDeactivate_ok(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.deactivate(1L);
        assertEquals(false, result.active());
        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    public void testDeactivate_hasActiveLoans(){
        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(true);
        assertThrows(BusinessException.class, ()-> userService.deactivate(1L));
        verify(userEntityRepository).hasActiveLoans(1L);
        verify(userEntityRepository, never()).findById(1L);
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testDeactivate_userAlreadyInactive(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        userEntity.setActive(false);
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.hasActiveLoans(1L)).thenReturn(false);

        assertThrows(BusinessException.class, ()-> userService.deactivate(1L));


        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testActivate_ok(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        userEntity.setActive(false);
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.activate(1L);
        assertEquals(true, result.active());

        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    public void testActivate_alreadyActive(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        assertThrows(BusinessException.class, ()-> userService.activate(1L));

        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testUpdate(){
        UserEntity userEntity = DataProvider.userListMock().get(1);
        UserEntityRequest userEntityRequest = new UserEntityRequest("messi@gmail.com", "Lionel modified", "Messi modified");
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntityResponse result = userService.update(1L, userEntityRequest);

        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(userEntity.getUsername(), result.username());

        verify(userEntityRepository).findById(1L);
        verify(userValidator).validateUser(userEntityRequest);
        verify(userEntityRepository).save(any(UserEntity.class));
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
}

package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserMapper;
import com.library.loansystem.Repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

import com.library.loansystem.Services.Validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp (){
        UserMapper userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper, userValidator);
    }

    @Test
    public void testFindAll(){
        List<User> userList = DataProvider.userListMock();

        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponse> result = userService.findAll();

        assertEquals(userList.size(), result.size());
        assertEquals(userList.get(1).getEmail(), result.get(1).email());
        verify(userRepository).findAll();
    }

    @Test
    public void testFindById(){
        User user = DataProvider.userListMock().get(1);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        UserResponse result = userService.findById(2L);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.username());
        verify(userRepository).findById(2L);
    }

    @Test
    public void testSave (){
        UserRequest userRequest = new UserRequest("angeldimaria@gmail.com", "fideo", "dimaria");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.save(userRequest);
        assertEquals(userRequest.email(), result.email());
        assertEquals(userRequest.username(), result.username());
        verify(userRepository).save(any(User.class));
        verify(userValidator).validateUser(userRequest);
    }

    @Test
    public void testDelete_ok(){
        User user = DataProvider.userListMock().get(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        userService.deletePermanently(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(any(User.class));
    }

    @Test
    public void testDelete_hasActiveLoans(){
        User user = DataProvider.userListMock().get(1);
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.hasActiveLoans(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.deletePermanently(1L));

        verify(userRepository).findById(1L);
        verify(userRepository).hasActiveLoans(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    public void testDeactivate_ok(){
        User user = DataProvider.userListMock().get(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.deactivate(1L);
        assertEquals(false, result.active());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testDeactivate_hasActiveLoans(){
        when(userRepository.hasActiveLoans(1L)).thenReturn(true);
        assertThrows(BusinessException.class, ()-> userService.deactivate(1L));
        verify(userRepository).hasActiveLoans(1L);
        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeactivate_userAlreadyInactive(){
        User user = DataProvider.userListMock().get(1);
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.hasActiveLoans(1L)).thenReturn(false);

        assertThrows(BusinessException.class, ()-> userService.deactivate(1L));


        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testActivate_ok(){
        User user = DataProvider.userListMock().get(1);
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.activate(1L);
        assertEquals(true, result.active());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testActivate_alreadyActive(){
        User user = DataProvider.userListMock().get(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, ()-> userService.activate(1L));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdate(){
        User user = DataProvider.userListMock().get(1);
        UserRequest userRequest = new UserRequest("messi@gmail.com", "Lionel modified", "Messi modified");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.update(1L, userRequest);

        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getUsername(), result.username());

        verify(userRepository).findById(1L);
        verify(userValidator).validateUser(userRequest);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testGetUserOrThrow_ok(){
        User user = DataProvider.userListMock().get(2);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        User result = userService.getUserOrThrow(2L);
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository).findById(2L);
    }

    @Test
    public void testGetUserOrThrow_NotFound(){

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserOrThrow(2L));
        verify(userRepository).findById(2L);
    }
}

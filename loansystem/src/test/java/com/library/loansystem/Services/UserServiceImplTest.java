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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanService loanService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp (){
        UserMapper userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, loanService, userMapper);
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
    public void testSave_ok (){
        UserRequest userRequest = new UserRequest("angeldimaria@gmail.com", "fideo", "dimaria");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.save(userRequest);
        assertEquals(userRequest.email(), result.email());
        assertEquals(userRequest.username(), result.username());
        verify(userRepository).save(any(User.class));
    }
    @Test
    public void testSave_BusinessException (){
        UserRequest userRequest = new UserRequest("angeldimaria@gmail.com", "fideo", "dimaria");
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(BusinessException.class, ()-> userService.save(userRequest));
        verify(userRepository, never()).save(any(User.class));
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

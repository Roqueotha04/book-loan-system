package com.library.loansystem.Services;

import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.DataProvider;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Mapper.UserMapper;
import com.library.loansystem.Repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp (){
        UserMapper userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
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
}

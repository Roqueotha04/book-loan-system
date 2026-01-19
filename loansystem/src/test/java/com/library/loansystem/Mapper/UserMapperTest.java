package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    UserMapper userMapper = new UserMapper();

    @Test
    public void testToResponse(){
        User user = new User("lionelmessi@hotmail.com", "lionel", "theGoat");

        UserResponse result = userMapper.toResponse(user);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getUsername(), result.username());
    }
}

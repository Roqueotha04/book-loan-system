package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.Entities.UserEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserEntityMapperTest {

    UserEntityMapper userEntityMapper = new UserEntityMapper();

    @Test
    public void testToResponse() {
        UserEntity userEntity = new UserEntity("lionelmessi@hotmail.com", "lionel", "theGoat");

        UserEntityResponse result = userEntityMapper.toResponse(userEntity);

        assertNull(result.id());
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(userEntity.getUsername(), result.username());
        assertEquals(userEntity.getActive(), result.active());
        assertEquals(userEntity.getRoles(), result.roles());
    }
}

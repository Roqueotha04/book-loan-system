package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse (User user){
       return new UserResponse(user.getId(),user.getEmail(),user.getUsername());
    }
}

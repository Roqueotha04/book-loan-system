package com.library.loansystem.Mapper;

import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.Entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public UserEntityResponse toResponse (UserEntity userEntity){
       return new UserEntityResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getUsername(), userEntity.getActive(), userEntity.getRoles());
    }
}

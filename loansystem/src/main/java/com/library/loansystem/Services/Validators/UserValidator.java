package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.UserEntityRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private final UserEntityRepository userEntityRepository;

    public UserValidator(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public void validateUser(UserEntityRequest userEntityRequest){
        if (userEntityRepository.existsByEmail(userEntityRequest.email())) throw new BusinessException("Email already in use");
        if (userEntityRepository.existsByUsername(userEntityRequest.username())) throw new BusinessException("Username already in use");
    }
}

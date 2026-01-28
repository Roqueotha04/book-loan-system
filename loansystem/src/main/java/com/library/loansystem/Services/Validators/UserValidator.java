package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUser(UserRequest userRequest){
        if (userRepository.existsByEmail(userRequest.email())) throw new BusinessException("Email already in use");
        if (userRepository.existsByUsername(userRequest.username())) throw new BusinessException("Username already in use");
    }
}

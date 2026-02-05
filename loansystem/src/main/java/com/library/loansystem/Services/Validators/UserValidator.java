package com.library.loansystem.Services.Validators;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Repositories.UserEntityRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

    public UserEntity validateUserRole (Long targetUserId, Authentication authentication){

        UserEntity userEntity;

        if (authentication.getAuthorities().stream()
                .anyMatch(auth-> auth.getAuthority().equals("ROLE_LIBRARIAN") || auth.getAuthority().equals("ROLE_ADMIN"))){
            userEntity= userEntityRepository.findById(targetUserId).orElseThrow(()-> new ResourceNotFoundException("Could not found user with Id: " + targetUserId));
        }else{
            userEntity = userEntityRepository.findByUsername(authentication.getName()).orElseThrow(()-> new ResourceNotFoundException("Could not found user with username: " + authentication.getName()));
            if (!userEntity.getId().equals(targetUserId)) throw new AccessDeniedException("Users can only act on it´s own account");
        }

        return userEntity;
    }
}

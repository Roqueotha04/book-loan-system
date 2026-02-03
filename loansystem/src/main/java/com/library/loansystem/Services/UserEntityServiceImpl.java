package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserEntityMapper;
import com.library.loansystem.Repositories.RoleRepository;
import com.library.loansystem.Repositories.UserEntityRepository;
import com.library.loansystem.Services.Validators.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;
    private final UserValidator userValidator;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepository, RoleRepository roleRepository, UserEntityMapper userEntityMapper, UserValidator userValidator) {
        this.userEntityRepository = userEntityRepository;
        this.roleRepository = roleRepository;
        this.userEntityMapper = userEntityMapper;
        this.userValidator = userValidator;
    }

    public List<UserEntityResponse> findAll(){
        return userEntityRepository.findAll().stream()
                .map(userEntityMapper::toResponse)
                .toList();
    }

    @Override
    public UserEntityResponse findById(Long id) {
        return userEntityMapper.toResponse(getUserOrThrow(id));
    }

    @Override
    public List<UserEntityResponse> searchByUsername(String username) {
        return userEntityRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(userEntityMapper::toResponse)
                .toList();
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserEntityResponse findByEmail(String email) {
        UserEntity userEntity = userEntityRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cannot found user with email: " +email));
        return userEntityMapper.toResponse(userEntity);
    }

    @Override
    public UserEntityResponse save(UserEntityRequest userEntityRequest) {
        userValidator.validateUser(userEntityRequest);
       UserEntity userEntity = toUser(userEntityRequest);
        Role defaultRole = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role ROLE_USER not found in DB"));
        Set<Role> roles = Set.of(defaultRole);
       userEntity.setRoles(roles);
       return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public void deletePermanently(Long id) {
        UserEntity userEntity = getUserOrThrow(id);
        validateNoActiveLoans(userEntity.getId());
        userEntityRepository.delete(userEntity);
    }

    @Override
    public UserEntityResponse deactivate(Long id) {
        validateNoActiveLoans(id);
        UserEntity userEntity = getUserOrThrow(id);
        if (!userEntity.getActive()) throw new BusinessException("User is already inactive");
        userEntity.setActive(false);
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntityResponse activate(Long id) {
        UserEntity userEntity = getUserOrThrow(id);
        if (userEntity.getActive()) throw new BusinessException("User is already active");
        userEntity.setActive(true);
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntityResponse update(Long id, UserEntityRequest userEntityRequest) {
        userValidator.validateUser(userEntityRequest);
        UserEntity userEntity = getUserOrThrow(id);
        userEntity.setEmail(userEntityRequest.email());
        userEntity.setUsername(userEntityRequest.username());
        userEntity.setPassword(userEntityRequest.password());
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntity getUserOrThrow(Long id) {
        return userEntityRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserEntity toUser (UserEntityRequest userEntityRequest){
        return new UserEntity(userEntityRequest.email(), userEntityRequest.username(), userEntityRequest.password());
    }

    private void validateNoActiveLoans(Long userId) {
        if (userEntityRepository.hasActiveLoans(userId)) {
            throw new BusinessException("User has active loans");
        }
    }


}

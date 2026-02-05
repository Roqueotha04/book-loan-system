package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Request.UserEntityUpdateRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.DTO.Response.UserEntityUpdateResponse;
import com.library.loansystem.DTO.Security.ResetPasswordRequest;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserEntityMapper;
import com.library.loansystem.Repositories.RoleRepository;
import com.library.loansystem.Repositories.UserEntityRepository;
import com.library.loansystem.Services.Validators.UserValidator;
import com.library.loansystem.Utils.JwtUtils;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepository, RoleRepository roleRepository, UserEntityMapper userEntityMapper, UserValidator userValidator, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userEntityRepository = userEntityRepository;
        this.roleRepository = roleRepository;
        this.userEntityMapper = userEntityMapper;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
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
        userValidator.validateUser(userEntityRequest.email(), userEntityRequest.username());
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
    public UserEntityResponse deactivate(Long id, Authentication auth) {
        validateNoActiveLoans(id);
        UserEntity userEntity = userValidator.validateUserRole(id, auth);
        if (!userEntity.getActive()) throw new BusinessException("User is already inactive");
        userEntity.setActive(false);
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntityResponse activate(Long id, Authentication auth) {
        UserEntity userEntity = userValidator.validateUserRole(id, auth);
        if (userEntity.getActive()) throw new BusinessException("User is already active");
        userEntity.setActive(true);
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntityUpdateResponse update(Long id, UserEntityUpdateRequest userEntityUpdateRequest, Authentication auth) {
        UserEntity userEntity = userValidator.validateUserRole(id, auth);
        userValidator.validateUpdateUser(userEntity, userEntityUpdateRequest.email(), userEntityUpdateRequest.username());
        String oldUsername = userEntity.getUsername();
        userEntity.setEmail(userEntityUpdateRequest.email());
        userEntity.setUsername(userEntityUpdateRequest.username());

        String newToken = null;
        if (auth.getName().equals(oldUsername)) {
            newToken = jwtUtils.createTokenFromEntity(userEntity);
        }

        UserEntity updatedUser = userEntityRepository.save(userEntity);
        return new UserEntityUpdateResponse(userEntityMapper.toResponse(updatedUser), newToken);
    }

    @Override
    public UserEntityResponse changePassword(Long userId, ResetPasswordRequest resetPasswordRequest, Authentication auth) {
        UserEntity userEntity = userValidator.validateUserRole(userId, auth);
        userValidator.validatePassword(userEntity, resetPasswordRequest.currentPassword(), resetPasswordRequest.newPassword());
        userEntity.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));

        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntityResponse changeRoles(Long userId, List<String> roleList) {
        Set <Role> userRoles = roleList.stream()
                .map(role -> roleRepository.findByRole(role).orElseThrow(()-> new ResourceNotFoundException("Role not found with name: " + role)))
                .collect(Collectors.toSet());
        if (userRoles.isEmpty()) {throw new BusinessException("User must have at least one role");}
        UserEntity userEntity = getUserOrThrow(userId);
        userEntity.setRoles(userRoles);
        return userEntityMapper.toResponse(userEntityRepository.save(userEntity));
    }

    @Override
    public UserEntity getUserOrThrow(Long id) {
        return userEntityRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserEntity toUser (UserEntityRequest userEntityRequest){
        return new UserEntity(userEntityRequest.email(), userEntityRequest.username(), passwordEncoder.encode(userEntityRequest.password()));
    }

    private void validateNoActiveLoans(Long userId) {
        if (userEntityRepository.hasActiveLoans(userId)) {
            throw new BusinessException("User has active loans");
        }
    }


}

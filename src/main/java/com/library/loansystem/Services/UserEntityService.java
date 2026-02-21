package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Request.UserEntityUpdateRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.DTO.Response.UserEntityUpdateResponse;
import com.library.loansystem.DTO.Security.ResetPasswordRequest;
import com.library.loansystem.Entities.Role;
import com.library.loansystem.Entities.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserEntityService {
    public List<UserEntityResponse> findAll();
    public UserEntityResponse findById(Long id);
    public List<UserEntityResponse> searchByUsername(String username);
    public UserEntity findByUsername (String username);
    public UserEntityResponse findByEmail(String email);
    public UserEntityResponse save(UserEntityRequest userEntityRequest);
    public void deletePermanently (Long id);
    public UserEntityResponse deactivate(Long id, Authentication auth);
    public UserEntityResponse activate(Long id, Authentication auth);
    public UserEntityUpdateResponse update (Long id, UserEntityUpdateRequest userEntityUpdateRequest, Authentication auth);
    public UserEntityResponse changePassword(Long userId, ResetPasswordRequest resetPasswordRequest, Authentication auth);
    public UserEntityResponse changeRoles(Long userId, List<String> roleList);
    public UserEntity getUserOrThrow(Long id);
}

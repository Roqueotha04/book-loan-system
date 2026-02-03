package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserEntityRequest;
import com.library.loansystem.DTO.Response.UserEntityResponse;
import com.library.loansystem.Entities.UserEntity;

import java.util.List;

public interface UserEntityService {
    public List<UserEntityResponse> findAll();
    public UserEntityResponse findById(Long id);
    public List<UserEntityResponse> searchByUsername(String username);
    public UserEntity findByUsername (String username);
    public UserEntityResponse findByEmail(String email);
    public UserEntityResponse save(UserEntityRequest userEntityRequest);
    public void deletePermanently (Long id);
    public UserEntityResponse deactivate(Long id);
    public UserEntityResponse activate(Long id);
    public UserEntityResponse update (Long id, UserEntityRequest userEntityRequest);
    public UserEntity getUserOrThrow(Long id);
}

package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;

import java.util.List;

public interface UserService {
    public List<UserResponse> findAll();
    public UserResponse findById(Long id);
    public List<UserResponse> searchByUsername(String username);
    public UserResponse findByEmail(String email);
    public UserResponse save(UserRequest userRequest);
    public void deletePermanently (Long id);
    public UserResponse deactivate(Long id);
    public UserResponse activate(Long id);
    public UserResponse update (Long id, UserRequest userRequest);
    public User getUserOrThrow(Long id);
}

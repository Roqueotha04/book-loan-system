package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;

import java.util.List;

public interface UserService {
    public List<UserResponse> findAll();
    public UserResponse findById(Long id);
    public UserResponse save(UserRequest userRequest);
    public void delete (Long id);
    public UserResponse update (Long id, UserRequest userRequest);
}

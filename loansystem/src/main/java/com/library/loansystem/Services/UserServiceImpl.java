package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Mapper.UserMapper;
import com.library.loansystem.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll(){
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return null;
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        return null;
    }

}

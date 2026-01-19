package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
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
        return userMapper.toResponse(getUserOrThrow(id));
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
       if (userRepository.existsByEmail(userRequest.email())) throw new BusinessException("Email already in use");
       if (userRepository.existsByUsername(userRequest.username())) throw new BusinessException("Username already in use");
       User user = toUser(userRequest);
       return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        return null;
    }

    @Override
    public User getUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));
    }

    private User toUser (UserRequest userRequest){
        return new User(userRequest.email(), userRequest.username(), userRequest.password());
    }


}

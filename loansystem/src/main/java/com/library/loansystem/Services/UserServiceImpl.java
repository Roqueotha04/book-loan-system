package com.library.loansystem.Services;

import com.library.loansystem.DTO.Request.UserRequest;
import com.library.loansystem.DTO.Response.UserResponse;
import com.library.loansystem.Entities.User;
import com.library.loansystem.Exceptions.BusinessException;
import com.library.loansystem.Exceptions.ResourceNotFoundException;
import com.library.loansystem.Mapper.UserMapper;
import com.library.loansystem.Repositories.UserRepository;
import com.library.loansystem.Services.Validators.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
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
        userValidator.validateUser(userRequest);
       User user = toUser(userRequest);
       return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deletePermanently(Long id) {
        User user = getUserOrThrow(id);
        validateNoActiveLoans(user.getId());
        userRepository.delete(user);
    }

    @Override
    public UserResponse deactivate(Long id) {
        validateNoActiveLoans(id);
        User user = getUserOrThrow(id);
        if (!user.getActive()) throw new BusinessException("User is already inactive");
        user.setActive(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse activate(Long id) {
        User user = getUserOrThrow(id);
        if (user.getActive()) throw new BusinessException("User is already active");
        user.setActive(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        userValidator.validateUser(userRequest);
        User user = getUserOrThrow(id);
        user.setEmail(userRequest.email());
        user.setUsername(userRequest.username());
        user.setPassword(userRequest.password());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public User getUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));
    }

    private User toUser (UserRequest userRequest){
        return new User(userRequest.email(), userRequest.username(), userRequest.password());
    }

    private void validateNoActiveLoans(Long userId) {
        if (userRepository.hasActiveLoans(userId)) {
            throw new BusinessException("User has active loans");
        }
    }


}

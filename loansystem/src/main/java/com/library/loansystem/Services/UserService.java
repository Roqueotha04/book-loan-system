package com.library.loansystem.Services;

import com.library.loansystem.Entities.User;

import java.util.List;

public interface UserService {
    public List<User> findAll();
    public User findById(Long id);
    public User save(User user);
    public void delete (Long id);
    public User update (Long id, User user);
}

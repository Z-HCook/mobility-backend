package com.wasel.backend.usecase;

import com.wasel.backend.model.User;
import com.wasel.backend.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUseCase {

    private final UserService userService;

    public UserUseCase(UserService userService) {
        this.userService = userService;
    }

    public User create(User user) {
        return userService.createUser(user);
    }

    public List<User> getAll() {
        return userService.getAllUsers();
    }

    public User getById(int id) {
        return userService.getUserById(id);
    }

    public void delete(int id) {
        userService.deleteUser(id);
    }
}
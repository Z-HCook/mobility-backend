
package com.wasel.backend.controller;

import com.wasel.backend.model.User;
import com.wasel.backend.usecase.UserUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userUseCase.create(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userUseCase.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userUseCase.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userUseCase.delete(id);
    }
}
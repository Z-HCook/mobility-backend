package com.wasel.backend.controller;

import com.wasel.backend.model.User;
import com.wasel.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ✅ Create
    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    // ✅ Get All
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    // ✅ Get By Id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        service.deleteUser(id);
    }
}
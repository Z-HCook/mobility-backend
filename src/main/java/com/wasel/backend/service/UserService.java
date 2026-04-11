package com.wasel.backend.service;

import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return repo.save(user);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(int id) {
        repo.deleteById(id);
    }

    public User getModerator(Integer id) {

        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == null ||
                (!user.getRole().equalsIgnoreCase("admin") &&
                        !user.getRole().equalsIgnoreCase("moderator"))) {
            throw new RuntimeException("Unauthorized");
        }

        return user;
    }
}
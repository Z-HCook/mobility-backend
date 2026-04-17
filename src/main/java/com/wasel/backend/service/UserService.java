package com.wasel.backend.service;

import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @CacheEvict(value = "users_list", allEntries = true)
    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return repo.save(user);
    }

    @Cacheable(value = "users_list")
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Cacheable(value = "users", key = "#id")
    public User getUserById(int id) {
        return repo.findById(id).orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users_list", allEntries = true)
    })
    public void deleteUser(int id) {
        repo.deleteById(id);
    }
}
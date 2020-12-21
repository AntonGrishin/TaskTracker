package com.tasktracker.application.security.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tasktracker.application.models.User;
import com.tasktracker.application.repository.UserRepository;

@Component
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long Id) {
        return userRepository.findById(Id).get();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}

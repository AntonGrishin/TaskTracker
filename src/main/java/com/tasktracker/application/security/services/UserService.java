package com.tasktracker.application.security.services;

import java.util.List;

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

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}

package com.tasktracker.application.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.tasktracker.application.payload.request.LoginRequest;
import com.tasktracker.application.payload.request.SignupRequest;
import com.tasktracker.application.payload.response.JwtResponse;
import com.tasktracker.application.payload.response.MessageResponse;
import com.tasktracker.application.repository.RoleRepository;
import com.tasktracker.application.repository.UserRepository;
import com.tasktracker.application.security.jwt.JwtUtils;
import com.tasktracker.application.security.services.UserDetailsImpl;

import com.tasktracker.application.models.User;
import com.tasktracker.application.links.UserLinks;
import com.tasktracker.application.security.services.UserService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/api/")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(path = UserLinks.LIST_USERS)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listUsers() {
        log.info("UsersController:  list users");
        List<User> resource = userService.getUsers();
        return ResponseEntity.ok(resource);
    }

    @RequestMapping(value = UserLinks.UPDATE_USER, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        log.info("UsersController:  update user");
        User resource = userService.updateUser(user);
        return ResponseEntity.ok(resource);
    }

    @RequestMapping(value = UserLinks.DELETE_USER, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(@RequestBody User user) {
        log.info("UsersController:  update user");
        userService.deleteUser(user);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable("id") long id) {

        User user = userService.getUser(id);

        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}

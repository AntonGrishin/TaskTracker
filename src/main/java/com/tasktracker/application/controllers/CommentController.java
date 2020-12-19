package com.tasktracker.application.controllers;

import org.springframework.http.HttpStatus;
import com.tasktracker.application.links.CommentLinks;
import com.tasktracker.application.links.UserLinks;
import com.tasktracker.application.models.Comment;
import com.tasktracker.application.models.User;
import com.tasktracker.application.payload.request.LoginRequest;
import com.tasktracker.application.payload.request.SignupRequest;
import com.tasktracker.application.payload.response.JwtResponse;
import com.tasktracker.application.payload.response.MessageResponse;
import com.tasktracker.application.repository.CommentRepository;
import com.tasktracker.application.repository.RoleRepository;
import com.tasktracker.application.repository.TaskRepository;
import com.tasktracker.application.repository.UserRepository;
import com.tasktracker.application.security.jwt.JwtUtils;
import com.tasktracker.application.security.services.CommentService;
import com.tasktracker.application.security.services.UserDetailsImpl;
import com.tasktracker.application.security.services.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/api/")
public class CommentController {

  @Autowired
  CommentRepository commentRepository;

  @PostMapping(path = CommentLinks.ADD_COMMENT)
  public ResponseEntity<?> addComment(@RequestBody Comment comment) {
    try {
      commentRepository.save( new Comment(
        comment.getUserId(),
        comment.getTaskId(),
        comment.getComment(),
        comment.getDate()
    ));
      log.info("CommentController:  add comment");
      return new ResponseEntity<>(new MessageResponse("Comment has been added successfully!"), HttpStatus.OK);
    } catch (Exception e) {
      
      return new ResponseEntity<>(new MessageResponse("Server error!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
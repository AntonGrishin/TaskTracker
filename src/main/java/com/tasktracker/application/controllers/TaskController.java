package com.tasktracker.application.controllers;

import com.tasktracker.application.links.TaskLinks;
import com.tasktracker.application.models.Comment;
import com.tasktracker.application.models.ERole;
import com.tasktracker.application.models.Role;
import com.tasktracker.application.models.Task;
import com.tasktracker.application.models.User;
import com.tasktracker.application.payload.request.LoginRequest;
import com.tasktracker.application.payload.request.SignupRequest;
import com.tasktracker.application.payload.response.JwtResponse;
import com.tasktracker.application.payload.response.MessageResponse;
import com.tasktracker.application.repository.CommentRepository;
import com.tasktracker.application.repository.CommentRepository;
import com.tasktracker.application.repository.RoleRepository;
import com.tasktracker.application.repository.TaskRepository;
import com.tasktracker.application.repository.UserRepository;
import com.tasktracker.application.security.jwt.JwtUtils;
import com.tasktracker.application.security.services.TaskService;
import com.tasktracker.application.security.services.UserDetailsImpl;
import com.tasktracker.application.security.services.UserService;
import com.tasktracker.application.security.services.UserDetailsImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class TaskController {

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  CommentRepository commentRepository;
  
  @Autowired
  UserService userService;

  @GetMapping("/tasks")
  public ResponseEntity<List<Task>> getAllTasks(
    @RequestParam(required = false) String taskTitle
  ) {
    try {

      UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();

      List<Task> tasks = new ArrayList<Task>();

      List<String> roles = userDetails.getAuthorities().stream()
      .map(item -> item.getAuthority())
      .collect(Collectors.toList());

      boolean isModAdm = roles.contains("ROLE_MODERATOR") || roles.contains("ROLE_ADMIN");
      if(isModAdm)
      {
        if (taskTitle == null) taskRepository
            .findAll()
            .forEach(tasks::add); else taskRepository
            .findByTaskTitleContaining(taskTitle)
            .forEach(tasks::add);
      }
      else
      {
        if (taskTitle == null) taskRepository
            .findByAssigned(userDetails.getUsername())
            .forEach(tasks::add); else taskRepository
            .findByTaskTitleContainingAndAssigned(taskTitle, userDetails.getUsername())
            .forEach(tasks::add);
      }

      if (tasks.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      for (Task task : tasks) {
          User user = userService.getUserByUsername(task.getAssigned());
          task.setAssigned(user.getFullname());
      }
      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> getTaskById(@PathVariable("id") long id) {
  
    Optional<Task> taskData = taskRepository.findById(id);

    if (taskData.isPresent()) {
      Task task = taskData.get();

      List<Comment> comments = new ArrayList<Comment>();
      String task_id = Long.toString(id);
      comments = commentRepository.findByTaskId(task_id);

      for (Comment comment : comments) {
        User cur_user = userService.getUser(Long.parseLong(comment.getUserId()));
        comment.setUserId(cur_user.getFullname());
      }
      task.setComments(comments);

      return new ResponseEntity<Task>(task, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/tasks")
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    try {
      Task _task = taskRepository.save(
        new Task(
          task.getTaskTitle(),
          task.getTaskDescription(),
          task.getEta(),
          task.getStartDate(),
          task.getAssigned(),
          task.getPoints(),
          false,
          task.getStatus()
        )
      );
      return new ResponseEntity<>(_task, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/tasks/{task_id}")
  public ResponseEntity<Task> updateTask(
    @PathVariable("task_id") long task_id,
    @RequestBody Task task
  ) {
    Optional<Task> taskData = taskRepository.findById(task_id);

    if (taskData.isPresent()) {
      Task _task = taskData.get();
      _task.setTaskTitle(task.getTaskTitle());
      _task.setTaskDescription(task.getTaskDescription());
      _task.setEta(task.getEta());
      _task.setStartDate(task.getStartDate());
      _task.setAssigned(task.getAssigned());
      _task.setPoints(task.getPoints());
      _task.setResolved(task.isResolved());
      _task.setStatus(task.getStatus());
      return new ResponseEntity<>(taskRepository.save(_task), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/tasks/{task_id}")
  public ResponseEntity<HttpStatus> deleteTask(
    @PathVariable("task_id") long task_id
  ) {
    try {
      taskRepository.deleteById(task_id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/tasks")
  public ResponseEntity<HttpStatus> deleteAllTasks() {
    try {
      taskRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tasks/resolved")
  public ResponseEntity<List<Task>> findByResolved() {
    try {
      List<Task> tasks = taskRepository.findByResolved(true);

      if (tasks.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

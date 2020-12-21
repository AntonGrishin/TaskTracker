package com.tasktracker.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tasktracker.application.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByResolved(boolean resolved);

  List<Task> findByTaskTitleContaining(String taskTitle);
  List<Task> findByTaskTitleContainingAndAssigned(String taskTitle, String assigned);
  List<Task> findByAssigned(String assigne);

  List<Task> findByResolvedAndAssigned(boolean resolved, String assigned);
}



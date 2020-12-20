package com.tasktracker.application.repository;

import java.util.List;

import com.tasktracker.application.models.Bonus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long> {
  List<Bonus> findByMonth(String month);
  List<Bonus> findByUserIdAndMonth(String userId, String Month);
  List<Bonus> findByUserIdAndMonthAndYear(String userId, String Month, String Year);
}



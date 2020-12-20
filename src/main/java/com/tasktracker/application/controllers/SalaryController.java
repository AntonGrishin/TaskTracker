package com.tasktracker.application.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasktracker.application.models.Bonus;
import com.tasktracker.application.models.Salary;
import com.tasktracker.application.models.Task;
import com.tasktracker.application.models.User;
import com.tasktracker.application.repository.BonusRepository;
import com.tasktracker.application.repository.TaskRepository;
import com.tasktracker.application.repository.UserRepository;
import com.tasktracker.application.security.jwt.JwtUtils;
import com.tasktracker.application.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Slf4j
@RequestMapping("/api")
public class SalaryController {

    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    public Date toDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    @GetMapping("/salary/{user_id}/{month}/{year}")
    public ResponseEntity<?> getUserSalaryByMonth(@PathVariable("user_id") String user_id,
            @PathVariable("month") String month, @PathVariable("year") String year) throws ParseException {
        List<Bonus> bonusData = bonusRepository.findByUserIdAndMonthAndYear(user_id, month, year);
        User userData = userRepository.findById(Long.parseLong(user_id)).get();
        List<Task> taskData = taskRepository.findByResolvedAndAssigned(true, userData.getUsername());

        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Month.of(Integer.parseInt(month)));
        Float daysInMonth = Float.valueOf(yearMonth.lengthOfMonth());
        Float baseSalary = Float.parseFloat(userData.getBaseSalary());
        Float dailySalary = baseSalary / daysInMonth;
        Float fullSalary = 0f;

        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate last = yearMonth.atEndOfMonth();

        for (Task task : taskData) {
            // date store like yyyy-MM-dd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = sdf.parse(StringUtils.substring(task.getEta(), 0, 10));
            Date startDate = sdf.parse(StringUtils.substring(task.getStartDate(), 0, 10));

            if (endDate.after(toDate(firstOfMonth)) && endDate.before(toDate(last))
                    && startDate.after(toDate(firstOfMonth)) && startDate.before(toDate(last))) {
                Long estimatedDays = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
                Long points = Long.parseLong(task.getPoints());
                Float estimationCoef = estimatedDays.floatValue() / points.floatValue();

                fullSalary += dailySalary * estimationCoef * points.floatValue();
            }
        }

        for (Bonus bonus : bonusData) {
            fullSalary += Float.parseFloat(bonus.getAmount());
        }

        return new ResponseEntity<>(
                new Salary(userData.getLastname() + ", " + userData.getFirstname(), fullSalary.toString()),
                HttpStatus.OK);
    }
}

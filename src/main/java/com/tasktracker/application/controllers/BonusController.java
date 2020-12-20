package com.tasktracker.application.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import java.util.Optional;

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
import com.tasktracker.application.models.ERole;
import com.tasktracker.application.models.Role;
import com.tasktracker.application.models.User;
import com.tasktracker.application.payload.request.LoginRequest;
import com.tasktracker.application.payload.request.SignupRequest;
import com.tasktracker.application.payload.response.JwtResponse;
import com.tasktracker.application.payload.response.MessageResponse;
import com.tasktracker.application.repository.BonusRepository;
import com.tasktracker.application.repository.RoleRepository;
import com.tasktracker.application.repository.UserRepository;
import com.tasktracker.application.security.jwt.JwtUtils;
import com.tasktracker.application.security.services.UserDetailsImpl;
import com.tasktracker.application.links.BonusLinks;
import com.tasktracker.application.links.UserLinks;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Slf4j
@RequestMapping("/api")
public class BonusController {

    @Autowired
    BonusRepository bonusRepository;

    @PostMapping(path = BonusLinks.ADD_BONUS)
    public ResponseEntity<?> addBonus(@RequestBody Bonus bonus) {
        try {
            bonusRepository.save(new Bonus(bonus.getUserId(), bonus.getComment(), bonus.getMonth(), bonus.getYear(),
                    bonus.getAmount()));
            log.info("Bonus controller:  add bonus");
            return new ResponseEntity<>(new MessageResponse("Comment has been added successfully!"), HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(new MessageResponse("Server error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update_bonus/{bonus_id}")
    public ResponseEntity<Bonus> updateBonus(@PathVariable("bonus_id") long bonus_id, @RequestBody Bonus bonus) {
        Optional<Bonus> bonusData = bonusRepository.findById(bonus_id);

        if (bonusData.isPresent()) {
            Bonus _bonus = bonusData.get();
            _bonus.setUserId(bonus.getUserId());
            _bonus.setComment(bonus.getComment());
            _bonus.setMonth(bonus.getMonth());
            _bonus.setYear(bonus.getYear());
            _bonus.setAmount(bonus.getAmount());
            return new ResponseEntity<>(bonusRepository.save(_bonus), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/bonus/{user_id}/{month}/{year}")
    public ResponseEntity<?> getBonusByUserMonth(@PathVariable("user_id") String user_id,
            @PathVariable("month") String month, @PathVariable("year") String year) {
        List<Bonus> bonusData = bonusRepository.findByUserIdAndMonthAndYear(user_id, month, year);

        if (bonusData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bonusData, HttpStatus.OK);
    }
}

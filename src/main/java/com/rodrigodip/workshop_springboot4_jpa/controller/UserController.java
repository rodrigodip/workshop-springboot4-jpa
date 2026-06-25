package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "/users")
public class UserController {

        public final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @GetMapping
        public ResponseEntity<List<User>> FindAll() {
                List<User> usersList = userService.FindAll();
                return ResponseEntity.ok().body(usersList);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<User> findById(@PathVariable Long id) {
                User userFound = userService.FindByID(id);
                return ResponseEntity.ok().body(userFound);
        }

}

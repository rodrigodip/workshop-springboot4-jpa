package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.services.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

        @PostMapping
        public ResponseEntity<User> insert(@RequestBody User newUser) {
                newUser = userService.insert(newUser);
                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(newUser.getId())
                                .toUri();

                return ResponseEntity.created(uri).body(newUser);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                userService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @PutMapping(value = "/{id}")
        public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User newUserData) {
                User updatedUser = userService.update(id, newUserData);
                return ResponseEntity.ok().body(updatedUser);
        }
}

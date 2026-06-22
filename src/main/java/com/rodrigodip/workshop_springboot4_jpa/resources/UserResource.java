package com.rodrigodip.workshop_springboot4_jpa.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
        @GetMapping
        public ResponseEntity<User> FindAll() {
                User u = new User(1L, "Rodrigo", "rodrigodip@gmail.com", "(31)992614515", "12235");
                return ResponseEntity.ok().body(u);
        }
}

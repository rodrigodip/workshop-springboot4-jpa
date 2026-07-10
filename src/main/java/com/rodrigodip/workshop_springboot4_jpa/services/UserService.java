package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.repositories.UserRepository;

@Service
public class UserService {

        private final UserRepository userRepository;

        public UserService(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        public List<User> FindAll() {
                return userRepository.findAll();
        }

        public User FindByID(Long id) {
                Optional<User> userOptional = userRepository.findById(id);
                return userOptional
                                .orElseThrow(
                                                () -> new RuntimeException("User Not Found."));
        }

        public User insert(User user) {
                return userRepository.save(user);
        }

        public void delete(Long id) {
                userRepository.deleteById(id);
        }

        public User update(long id, User newUserData) {
                User foundUser = userRepository.getReferenceById(id);
                updateData(foundUser, newUserData);
                return userRepository.save(foundUser);
        }

        private void updateData(User foundUser, User newUserData) {
                foundUser.setName(newUserData.getName());
                foundUser.setEmail(newUserData.getEmail());
                foundUser.setPhone(newUserData.getPhone());
        }
}
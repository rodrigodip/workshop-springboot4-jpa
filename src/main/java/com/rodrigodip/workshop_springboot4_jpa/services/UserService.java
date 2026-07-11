package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.repositories.UserRepository;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.DataBaseException;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

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
                                .orElseThrow(() -> new ResourceNotFoundException(id));
        }

        public User insert(User user) {
                return userRepository.save(user);
        }

        public void delete(Long id) {
                try {
                        userRepository.deleteById(id);

                } catch (EmptyResultDataAccessException e) {
                        throw new ResourceNotFoundException(id);
                } catch (DataIntegrityViolationException e) {
                        throw new DataBaseException(e.getMessage());
                }
        }

        public User update(long id, User newUserData) {
                try {
                        User foundUser = userRepository.getReferenceById(id);
                        updateData(foundUser, newUserData);
                        return userRepository.save(foundUser);
                } catch (EntityNotFoundException e) {
                        throw new ResourceNotFoundException(id);
                }
        }

        private void updateData(User foundUser, User newUserData) {
                foundUser.setName(newUserData.getName());
                foundUser.setEmail(newUserData.getEmail());
                foundUser.setPhone(newUserData.getPhone());
        }
}
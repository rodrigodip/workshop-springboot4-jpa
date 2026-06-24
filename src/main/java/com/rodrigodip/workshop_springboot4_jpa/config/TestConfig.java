package com.rodrigodip.workshop_springboot4_jpa.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

        private final UserRepository userRepository;

        public TestConfig(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Override
        public void run(String... args) throws Exception {

                User u1 = new User(null, "Maria Brown", "maria@gmail.com", "988888888", "123456");
                User u2 = new User(null, "Alex Green", "alex@gmail.com", "977777777", "123456");
                User u3 = new User(null, "Rodrigo", "rod@gmail.com", "992614515", "098787");

                userRepository.saveAll(Arrays.asList(u1, u2, u3));
        }
}

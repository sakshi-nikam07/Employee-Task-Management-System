package com.example.taskmanager.config;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Seeds a default admin and one employee on first startup. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            create(users, encoder, "Admin", "admin@test.com", "admin123", "ADMIN");
            create(users, encoder, "John", "john@test.com", "john123", "EMPLOYEE");
        };
    }

    private void create(UserRepository users, PasswordEncoder encoder,
                        String name, String email, String pwd, String role) {
        if (users.findByEmail(email).isEmpty()) {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(encoder.encode(pwd));
            u.setRole(role);
            users.save(u);
        }
    }
}

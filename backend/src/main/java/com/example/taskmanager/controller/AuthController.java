package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /** Public registration always creates an EMPLOYEE (role cannot be chosen). */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User u) {
        if (u.getEmail() == null || u.getPassword() == null || u.getName() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "name, email and password are required"));
        if (users.findByEmail(u.getEmail()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        u.setId(null);
        u.setPassword(encoder.encode(u.getPassword()));
        u.setRole("EMPLOYEE");
        return ResponseEntity.ok(users.save(u));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        var user = users.findByEmail(body.get("email")).orElse(null);
        if (user == null || body.get("password") == null
                || !encoder.matches(body.get("password"), user.getPassword()))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        return ResponseEntity.ok(Map.of(
                "token", jwt.generate(user.getEmail(), user.getRole()),
                "role", user.getRole(),
                "name", user.getName()));
    }
}

package com.example.demo.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setName(user.getName());
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existing);
    }

    public User updatePreferences(Long userId, UserPreferences preferences) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setPreferences(preferences);

        return userRepository.save(user);
    }

    public User loginUser(User user) {

        User existingUser = null;

        // Login using email
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        }

        // Login using username
        if (existingUser == null &&
                user.getUsername() != null &&
                !user.getUsername().isBlank()) {
            existingUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        }

        if (existingUser != null &&
                passwordEncoder.matches(
                        user.getPassword(),
                        existingUser.getPassword())) {
            return existingUser;
        }

        return null;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
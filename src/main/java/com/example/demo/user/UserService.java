package com.example.demo.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // @Value("${google.client-id}")
    // private String googleClientId;

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

    // public User loginWithGoogle(String idTokenString) throws Exception {

    // GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
    // GoogleNetHttpTransport.newTrustedTransport(),
    // JacksonFactory.getDefaultInstance())
    // .setAudience(Collections.singletonList(googleClientId))
    // .build();

    // GoogleIdToken idToken = verifier.verify(idTokenString);

    // if (idToken == null) {
    // throw new RuntimeException("Invalid Google ID token");
    // }

    // GoogleIdToken.Payload payload = idToken.getPayload();

    // String googleId = payload.getSubject();
    // String email = payload.getEmail();
    // String name = (String) payload.get("name");
    // Boolean emailVerified = payload.getEmailVerified();

    // if (googleId == null || email == null || !Boolean.TRUE.equals(emailVerified))
    // {
    // throw new RuntimeException("Invalid Google account information");
    // }

    // // Existing Google account
    // User existingUser = userRepository.findByGoogleId(googleId)
    // .orElse(null);

    // if (existingUser != null) {
    // return existingUser;
    // }

    // // Existing normal account with same email
    // existingUser = userRepository.findByEmail(email)
    // .orElse(null);

    // if (existingUser != null) {
    // existingUser.setGoogleId(googleId);
    // return userRepository.save(existingUser);
    // }

    // // New Google account
    // User newUser = new User();
    // newUser.setGoogleId(googleId);
    // newUser.setEmail(email);
    // newUser.setName(name);

    // return userRepository.save(newUser);
    // }

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
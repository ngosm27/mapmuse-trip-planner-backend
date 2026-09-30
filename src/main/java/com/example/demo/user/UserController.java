package com.example.demo.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping
    // public ResponseEntity<List<User>> getUsers() {
    // List<User> users = userService.getUsers();
    // return ResponseEntity.ok(users);
    // }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        User loggedInUser = userService.loginUser(user);

        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        }

        return ResponseEntity.status(401).build();
    }

    // @PostMapping("/users/google")
    // public ResponseEntity<User> googleLogin(
    // @RequestBody Map<String, String> body) {

    // try {
    // String credential = body.get("credential");

    // User user = userService.loginWithGoogle(credential);

    // return ResponseEntity.ok(user);

    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(401).build();
    // }
    // }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        if (userService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(400).build();
        }

        User createdUser = userService.createUser(user);

        return ResponseEntity.status(201).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        User updatedUser = userService.updateUser(id, user);

        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserPreferences> updatePreferences(
            @PathVariable Long id,
            @RequestBody UserPreferences preferences) {

        return ResponseEntity.ok(
                userService.updatePreferences(id, preferences).getPreferences());
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user != null) {
            return ResponseEntity.ok(user.getPreferences());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords) {

        User existing = userService.getUserById(id);

        if (existing == null) {
            return ResponseEntity.status(404).build();
        }

        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Password fields are required");
        }

        if (!userService.checkPassword(currentPassword, existing.getPassword())) {
            return ResponseEntity
                    .status(400)
                    .body("Current password is incorrect");
        }

        existing.setPassword(newPassword);
        userService.updateUser(id, existing);

        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
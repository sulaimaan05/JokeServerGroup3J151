package Service;

import Model.User;
import Repository.UserRepo;

import java.sql.SQLException;
import java.util.Optional;


public class AuthService {

    private final UserRepo userRepo;

    public AuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> register(String username, String password, String email, String role) throws SQLException {
        if (username == null || username.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();
        if (email == null || email.isBlank()) return Optional.empty();
        if (!isValidRole(role)) return Optional.empty();

        // Prevent duplicate usernames
        Optional<User> existing = userRepo.getUserByUsername(username);
        if (existing.isPresent()) return Optional.empty();

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);   // Hash this in production
        newUser.setEmail(email);
        newUser.setRole(role);

        return userRepo.createUser(newUser);

    }

    public Optional<User> login(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();

        Optional<User> userOpt = userRepo.getUserByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        // Compare passwords (use BCrypt or similar in production)
        if (!user.getPassword().equals(password)) return Optional.empty();

        return Optional.of(user);
    }

    private boolean isValidRole(String role) {
        return role != null && (
                role.equals("user") ||
                        role.equals("joke_creator") ||
                        role.equals("moderator_pending")
        );
    }
}

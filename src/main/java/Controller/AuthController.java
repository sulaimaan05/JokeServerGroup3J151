package Controller;

import Controller.ControllerResponse;
import Model.User;
import Service.AuthService;

import java.sql.SQLException;
import java.util.Optional;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ControllerResponse<User> register(String username, String password, String email, String role) throws SQLException {
        if (username == null || username.isBlank())
            return ControllerResponse.failure("Username cannot be empty.");
        if (password == null || password.isBlank())
            return ControllerResponse.failure("Password cannot be empty.");
        if (email == null || email.isBlank())
            return ControllerResponse.failure("Email cannot be empty.");

        Optional<User> result = authService.register(username, password, email, role);

        return result
                .map(u -> ControllerResponse.success("Registration successful. Welcome, " + u.getUsername() + "!", u))
                .orElse(ControllerResponse.failure("Registration failed. Username may already be taken or role is invalid."));
    }

    public ControllerResponse<User> login(String username, String password) throws SQLException {
        if (username == null || username.isBlank())
            return ControllerResponse.failure("Username cannot be empty.");
        if (password == null || password.isBlank())
            return ControllerResponse.failure("Password cannot be empty.");

        Optional<User> result = authService.login(username, password);

        return result
                .map(u -> ControllerResponse.success("Login successful. Welcome back, " + u.getUsername() + "!", u))
                .orElse(ControllerResponse.failure("Invalid username or password."));
    }
}
package Service;

import Model.User;
import Repository.UserRepo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class UserService {

    private final UserRepo userRepo;

    // Roles ordered by privilege level (ascending)
    private static final List<String> ROLE_HIERARCHY = List.of(
            "user",
            "joke_creator",
            "moderator_pending",
            "moderator"
    );

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getUserById(int userId) throws SQLException {
        return userRepo.getUserById(userId);
    }

    public Optional<User> getUserByUsername(String username) throws SQLException {
        return userRepo.getUserByUsername(username);
    }

    public List<User> getAllUsers() throws SQLException {
        return userRepo.readAllUsers();
    }

    public boolean updateEmail(int userId, String newEmail) throws SQLException {
        if (newEmail == null || newEmail.isBlank()) return false;

        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setEmail(newEmail);
        userRepo.updateUser(user);
        return true;
    }

    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.isBlank()) return false;

        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(newPassword);
        userRepo.updateUser(user);
        return true;
    }

    public boolean updateUsername(int userId, String newUsername) throws SQLException {
        if (newUsername == null || newUsername.isBlank()) return false;

        // Ensure new username is not already taken
        Optional<User> conflict = userRepo.getUserByUsername(newUsername);
        if (conflict.isPresent()) return false;

        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setUsername(newUsername);
        userRepo.updateUser(user);
        return true;
    }

    public boolean upgradeRole(int userId, String newRole) throws SQLException {
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        String currentRole = user.getRole();

        int currentIndex = ROLE_HIERARCHY.indexOf(currentRole);
        int targetIndex  = ROLE_HIERARCHY.indexOf(newRole);

        // Must be a valid role and must be a promotion, not a lateral/demotion
        if (targetIndex <= currentIndex) return false;

        // Only allow one step at a time
        if (targetIndex - currentIndex != 1) return false;

        user.setRole(newRole);
        userRepo.updateUser(user);
        return true;
    }

    public boolean downgradeRole(int userId, String newRole) throws SQLException {
        Optional<User> userOpt = userRepo.getUserById(userId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        String currentRole = user.getRole();

        int currentIndex = ROLE_HIERARCHY.indexOf(currentRole);
        int targetIndex  = ROLE_HIERARCHY.indexOf(newRole);

        // Must be a valid role and must be a demotion
        if (targetIndex >= currentIndex || targetIndex < 0) return false;

        user.setRole(newRole);
        userRepo.updateUser(user);
        return true;
    }

    public boolean deleteAccount(String username) throws SQLException {
        return userRepo.deleteUserByUsername(username);
    }
}

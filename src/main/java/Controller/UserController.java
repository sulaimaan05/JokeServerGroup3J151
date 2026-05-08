package Controller;

import Controller.ControllerResponse;
import Model.User;
import Service.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class UserController {

    private final Service.UserService userService;

    public UserController(Service.UserService userService) {
        this.userService = userService;
    }

    public ControllerResponse<User> getUserById(int userId) throws SQLException {
        Optional<User> result = userService.getUserById(userId);
        return result
                .map(u -> ControllerResponse.success("User found.", u))
                .orElse(ControllerResponse.failure("No user found with ID: " + userId));
    }

    public ControllerResponse<List<User>> getAllUsers(String requesterRole) throws SQLException {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        List<User> users = userService.getAllUsers();
        return ControllerResponse.success("Retrieved " + users.size() + " user(s).", users);
    }

    public ControllerResponse<Void> updateEmail(int userId, String newEmail) throws SQLException {
        if (newEmail == null || newEmail.isBlank())
            return ControllerResponse.failure("Email cannot be empty.");

        boolean updated = userService.updateEmail(userId, newEmail);
        return updated
                ? ControllerResponse.success("Email updated successfully.")
                : ControllerResponse.failure("Failed to update email. User not found.");
    }

    public ControllerResponse<Void> updatePassword(int userId, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.isBlank())
            return ControllerResponse.failure("Password cannot be empty.");

        boolean updated = userService.updatePassword(userId, newPassword);
        return updated
                ? ControllerResponse.success("Password updated successfully.")
                : ControllerResponse.failure("Failed to update password. User not found.");
    }

    public ControllerResponse<Void> updateUsername(int userId, String newUsername) throws SQLException {
        if (newUsername == null || newUsername.isBlank())
            return ControllerResponse.failure("Username cannot be empty.");

        boolean updated = userService.updateUsername(userId, newUsername);
        return updated
                ? ControllerResponse.success("Username updated successfully.")
                : ControllerResponse.failure("Username is already taken or invalid.");
    }

    public ControllerResponse<Void> upgradeRole(int userId, String newRole) throws SQLException {
        boolean upgraded = userService.upgradeRole(userId, newRole);
        if (upgraded) {
            String msg = "moderator_pending".equals(newRole)
                    ? "Moderator request submitted. Awaiting approval."
                    : "Account upgraded to " + newRole + ".";
            return ControllerResponse.success(msg);
        }
        return ControllerResponse.failure("Role upgrade failed. Check that the target role is one level above your current role.");
    }

    public ControllerResponse<Void> downgradeRole(int userId, String newRole) throws SQLException {
        boolean downgraded = userService.downgradeRole(userId, newRole);
        return downgraded
                ? ControllerResponse.success("Account downgraded to " + newRole + ".")
                : ControllerResponse.failure("Role downgrade failed. Check that the target role is one level below your current role.");
    }

    public ControllerResponse<Void> deleteAccount(String username) throws SQLException {
        boolean deleted = userService.deleteAccount(username);
        return deleted
                ? ControllerResponse.success("Account deleted successfully.")
                : ControllerResponse.failure("Failed to delete account. User not found.");
    }
}
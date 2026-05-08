package Controller;

import Controller.ControllerResponse;
import Model.Joke;
import Model.User;
import Service.ModerationService;

import java.sql.SQLException;
import java.util.List;


public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    public ControllerResponse<List<Joke>> getPendingJokes(String moderatorRole) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        List<Joke> pending = moderationService.getPendingJokes(moderatorRole);
        if (pending.isEmpty())
            return ControllerResponse.success("No jokes are currently awaiting review.", pending);

        return ControllerResponse.success("Retrieved " + pending.size() + " pending joke(s).", pending);
    }

    public ControllerResponse<Void> approveJoke(String moderatorRole, int jokeId) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean approved = moderationService.approveJoke(moderatorRole, jokeId);
        return approved
                ? ControllerResponse.success("Joke #" + jokeId + " has been approved and is now public.")
                : ControllerResponse.failure("Failed to approve joke. It may not exist or is not in a pending state.");
    }

    public ControllerResponse<Void> rejectJoke(String moderatorRole, int jokeId) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean rejected = moderationService.rejectJoke(moderatorRole, jokeId);
        return rejected
                ? ControllerResponse.success("Joke #" + jokeId + " has been rejected.")
                : ControllerResponse.failure("Failed to reject joke. It may not exist or is not in a pending state.");
    }

    public ControllerResponse<List<User>> getPendingModeratorRequests(String moderatorRole) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        List<User> pending = moderationService.getPendingModeratorRequests(moderatorRole);
        if (pending.isEmpty())
            return ControllerResponse.success("No pending moderator requests at this time.", pending);

        return ControllerResponse.success("Retrieved " + pending.size() + " pending moderator request(s).", pending);
    }

    public ControllerResponse<Void> approveModeratorRequest(String moderatorRole, int targetUserId) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean approved = moderationService.approveModeratorRequest(moderatorRole, targetUserId);
        return approved
                ? ControllerResponse.success("User #" + targetUserId + " has been promoted to moderator.")
                : ControllerResponse.failure("Failed to approve request. User may not exist or is not pending moderator status.");
    }

    public ControllerResponse<Void> denyModeratorRequest(String moderatorRole, int targetUserId) throws SQLException {
        if (!"moderator".equals(moderatorRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean denied = moderationService.denyModeratorRequest(moderatorRole, targetUserId);
        return denied
                ? ControllerResponse.success("Moderator request for user #" + targetUserId + " has been denied.")
                : ControllerResponse.failure("Failed to deny request. User may not exist or is not pending moderator status.");
    }
}

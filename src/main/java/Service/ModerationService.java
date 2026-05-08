package Service;

import Model.Joke;
import Model.User;
import Repository.JokeRepo;
import Repository.UserRepo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ModerationService {

    private final JokeRepo jokeRepo;
    private final UserRepo userRepo;

    public ModerationService(JokeRepo jokeRepo, UserRepo userRepo) {
        this.jokeRepo = jokeRepo;
        this.userRepo = userRepo;
    }

    public List<Joke> getPendingJokes(String moderatorRole) throws SQLException {
        if (!isModerator(moderatorRole)) return List.of();
        return jokeRepo.getPendingJokes();
    }

    public boolean approveJoke(String moderatorRole, int jokeId) throws SQLException {
        if (!isModerator(moderatorRole)) return false;

        Optional<Joke> jokeOpt = jokeRepo.getJokeById(jokeId);
        if (jokeOpt.isEmpty()) return false;

        Joke joke = jokeOpt.get();
        if (!joke.getStatus().equals("pending")) return false;

        return jokeRepo.updateJokeStatus(jokeId, "approved");
    }

    public boolean rejectJoke(String moderatorRole, int jokeId) throws SQLException {
        if (!isModerator(moderatorRole)) return false;

        Optional<Joke> jokeOpt = jokeRepo.getJokeById(jokeId);
        if (jokeOpt.isEmpty()) return false;

        Joke joke = jokeOpt.get();
        if (!joke.getStatus().equals("pending")) return false;

        return jokeRepo.updateJokeStatus(jokeId, "rejected");
    }

    public List<User> getPendingModeratorRequests(String moderatorRole) throws SQLException {
        if (!isModerator(moderatorRole)) return List.of();

        return userRepo.readAllUsers().stream()
                .filter(u -> "moderator_pending".equals(u.getRole()))
                .toList();
    }

    public boolean approveModeratorRequest(String moderatorRole, int targetUserId) throws SQLException {
        if (!isModerator(moderatorRole)) return false;

        Optional<User> userOpt = userRepo.getUserById(targetUserId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        if (!"moderator_pending".equals(user.getRole())) return false;

        user.setRole("moderator");
        userRepo.updateUser(user);
        return true;
    }

    public boolean denyModeratorRequest(String moderatorRole, int targetUserId) throws SQLException {
        if (!isModerator(moderatorRole)) return false;

        Optional<User> userOpt = userRepo.getUserById(targetUserId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        if (!"moderator_pending".equals(user.getRole())) return false;

        user.setRole("user");
        userRepo.updateUser(user);
        return true;
    }

    private boolean isModerator(String role) {
        return "moderator".equals(role);
    }
}

package Service;

import Model.Joke;
import Repository.JokeRepo;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class JokeService {

    private final JokeRepo jokeRepo;

    // Allowed joke statuses
    public static final String STATUS_PENDING  = "pending";
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_REJECTED = "rejected";

    public JokeService(JokeRepo jokeRepo) {
        this.jokeRepo = jokeRepo;
    }

    public Optional<Joke> submitJoke(int creatorId, String creatorRole, String setup, String punchline, String category) {
        if (!canCreateJokes(creatorRole)) return Optional.empty();
        if (setup == null || setup.isBlank()) return Optional.empty();
        if (punchline == null || punchline.isBlank()) return Optional.empty();

        Joke joke = new Joke();
        joke.setCreatorId(creatorId);
        joke.setSetup(setup);
        joke.setPunchline(punchline);
        joke.setCategory(category != null ? category : "general");
        joke.setStatus(STATUS_PENDING);

        return jokeRepo.createJoke(joke);
    }

    public List<Joke> getApprovedJokes() {
        try {
            return jokeRepo.getApprovedJokes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Joke> getPendingJokes() {
        try {
            return jokeRepo.getPendingJokes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Joke> getJokesByCreator(int creatorId) {
        try {
            return jokeRepo.getJokesByCreator(creatorId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Joke> getAllJokes() {
        try {
            return jokeRepo.readAllJokes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Joke> getJokeById(int jokeId) {
        try {
            return jokeRepo.getJokeById(jokeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean editJoke(int requesterId, int jokeId, String newSetup, String newPunchline, String newCategory) throws SQLException {
        Optional<Joke> jokeOpt = jokeRepo.getJokeById(jokeId);
        if (jokeOpt.isEmpty()) return false;

        Joke joke = jokeOpt.get();

        // Only the original creator can edit their own joke
        if (joke.getCreatorId() != requesterId) return false;

        // Cannot edit an approved or rejected joke
        if (!joke.getStatus().equals(STATUS_PENDING)) return false;

        if (newSetup != null && !newSetup.isBlank())     joke.setSetup(newSetup);
        if (newPunchline != null && !newPunchline.isBlank()) joke.setPunchline(newPunchline);
        if (newCategory != null && !newCategory.isBlank()) joke.setCategory(newCategory);

        jokeRepo.updateJoke(joke);
        return true;
    }

    public boolean deleteJoke(int requesterId, String requesterRole, int jokeId) {
        Optional<Joke> jokeOpt = null;
        try {
            jokeOpt = jokeRepo.getJokeById(jokeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (jokeOpt.isEmpty()) return false;

        Joke joke = jokeOpt.get();

        boolean isModerator = "moderator".equals(requesterRole);
        boolean isOwner     = joke.getCreatorId() == requesterId;

        if (!isModerator && !isOwner) return false;

        try {
            return jokeRepo.deleteJokeById(jokeId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean canCreateJokes(String role) {
        return "joke_creator".equals(role) || "moderator".equals(role);
    }
}

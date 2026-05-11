package Controller;

import Model.Joke;
import Service.JokeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JokeController {

    private final JokeService jokeService;

    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    public ControllerResponse<Joke> submitJoke(int creatorId, String creatorRole, String jokeText) throws SQLException {
        if (jokeText == null || jokeText.isBlank())
            return ControllerResponse.failure("Joke text cannot be empty.");

        Optional<Joke> result = jokeService.submitJoke(creatorId, creatorRole, jokeText);
        return result
            .map(j -> ControllerResponse.success("Joke submitted for review! It will be visible once approved.", j))
            .orElse(ControllerResponse.failure("Failed to submit joke. Only joke creators and moderators may submit jokes."));
    }

    public ControllerResponse<List<Joke>> getApprovedJokes() {
        List<Joke> jokes = jokeService.getApprovedJokes();
        if (jokes.isEmpty())
            return ControllerResponse.success("No approved jokes available yet.", jokes);
        return ControllerResponse.success("Retrieved " + jokes.size() + " joke(s).", jokes);
    }

    public ControllerResponse<List<Joke>> getPendingJokes(String requesterRole) {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        List<Joke> jokes = jokeService.getPendingJokes();
        return ControllerResponse.success("Retrieved " + jokes.size() + " pending joke(s).", jokes);
    }

    public ControllerResponse<List<Joke>> getMyJokes(int creatorId, String creatorRole) {
        if (!"creator".equals(creatorRole) && !"moderator".equals(creatorRole))
            return ControllerResponse.failure("Access denied. Only joke creators can view their submitted jokes.");

        List<Joke> jokes = jokeService.getJokesByCreator(creatorId);
        return ControllerResponse.success("Retrieved " + jokes.size() + " of your joke(s).", jokes);
    }

    public ControllerResponse<List<Joke>> getAllJokes(String requesterRole) {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        List<Joke> jokes = jokeService.getAllJokes();
        return ControllerResponse.success("Retrieved " + jokes.size() + " total joke(s).", jokes);
    }

    public ControllerResponse<Joke> getJokeById(int jokeId, String requesterRole) {
        Optional<Joke> jokeOpt = jokeService.getJokeById(jokeId);
        if (jokeOpt.isEmpty())
            return ControllerResponse.failure("Joke not found.");

        Joke joke = jokeOpt.get();

        if (!"moderator".equals(requesterRole) && !"approved".equals(joke.getStatus()))
            return ControllerResponse.failure("This joke is not yet available for public viewing.");

        return ControllerResponse.success("Joke retrieved.", joke);
    }

    public ControllerResponse<Void> editJoke(int requesterId, int jokeId, String newJokeText) throws SQLException {
        if (newJokeText == null || newJokeText.isBlank())
            return ControllerResponse.failure("New joke text cannot be empty.");

        boolean updated = jokeService.editJoke(requesterId, jokeId, newJokeText);
        return updated
            ? ControllerResponse.success("Joke updated successfully.")
            : ControllerResponse.failure("Failed to update joke. You may only edit your own pending jokes.");
    }

    public ControllerResponse<Void> moderateJoke(int jokeId, String requesterRole, String newStatus) throws SQLException {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean updated = jokeService.moderateJoke(jokeId, newStatus);
        return updated
            ? ControllerResponse.success("Joke status updated to: " + newStatus + ".")
            : ControllerResponse.failure("Failed to update joke status. Invalid status or joke not found.");
    }

    public ControllerResponse<Void> deleteJoke(int requesterId, String requesterRole, int jokeId) {
        boolean deleted = jokeService.deleteJoke(requesterId, requesterRole, jokeId);
        return deleted
            ? ControllerResponse.success("Joke deleted successfully.")
            : ControllerResponse.failure("Failed to delete joke. You may only delete your own jokes.");
    }
}
package Controller;

import Controller.ControllerResponse;
import Model.JokeOfTheDay;
import Service.JokeOfTheDayService;

import java.util.Optional;


public class JokeOfTheDayController {

    private final JokeOfTheDayService jodService;

    public JokeOfTheDayController(JokeOfTheDayService jodService) {
        this.jodService = jodService;
    }

    public ControllerResponse<JokeOfTheDay> getJokeOfTheDay() {
        Optional<JokeOfTheDay> result = jodService.getJokeOfTheDay();
        return result
                .map(j -> ControllerResponse.success("Here is today's Joke of the Day!", j))
                .orElse(ControllerResponse.failure("No Joke of the Day is available yet. Check back soon!"));
    }

    public ControllerResponse<JokeOfTheDay> getJokeOfTheDayById(int id) {
        Optional<JokeOfTheDay> result = jodService.getJokeOfTheDayById(id);
        return result
                .map(j -> ControllerResponse.success("Joke of the Day record retrieved.", j))
                .orElse(ControllerResponse.failure("No Joke of the Day record found with ID: " + id));
    }

    public ControllerResponse<JokeOfTheDay> refreshJokeOfTheDay(String requesterRole) {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Only moderators may manually refresh the Joke of the Day.");

        Optional<JokeOfTheDay> result = jodService.refreshJokeOfTheDay();
        return result
                .map(j -> ControllerResponse.success("Joke of the Day has been refreshed!", j))
                .orElse(ControllerResponse.failure("Could not refresh Joke of the Day. No eligible jokes found in the last 24 hours."));
    }

    public ControllerResponse<Void> updateVoteCount(int jodId, int totalVotes) {
        jodService.updateVoteCount(jodId, totalVotes);
        return ControllerResponse.success("Joke of the Day vote count updated to " + totalVotes + ".");
    }

    public ControllerResponse<Void> deleteJokeOfTheDayById(String requesterRole, int id) {
        if (!"moderator".equals(requesterRole))
            return ControllerResponse.failure("Access denied. Moderator role required.");

        boolean deleted = jodService.deleteJokeOfTheDayById(id);
        return deleted
                ? ControllerResponse.success("Joke of the Day record #" + id + " deleted.")
                : ControllerResponse.failure("Record not found with ID: " + id);
    }
}

package Service;

import Model.JokeOfTheDay;
import Repository.JokeOfTheDayRepo;

import java.sql.SQLException;
import java.util.Optional;

public class JokeOfTheDayService {

    private final JokeOfTheDayRepo jodRepo;

    public JokeOfTheDayService(JokeOfTheDayRepo jodRepo) {
        this.jodRepo = jodRepo;
    }

    public Optional<JokeOfTheDay> getJokeOfTheDay() {
        try {
            return jodRepo.getJokeOfTheDay();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<JokeOfTheDay> getJokeOfTheDayById(int id) {
        try {
            return jodRepo.getJokeOfTheDayById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<JokeOfTheDay> refreshJokeOfTheDay() {
        try {
            return jodRepo.refreshJokeOfTheDay();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateVoteCount(int jodId, int totalVotes) {
        try {
            jodRepo.updateTotalVotes(jodId, totalVotes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteJokeOfTheDayById(int id) {
        try {
            return jodRepo.deleteJokeOfTheDayById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

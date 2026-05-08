package Repository;

import Model.JokeOfTheDay;
import java.sql.*;
import java.util.Optional;

public class JokeOfTheDayRepo implements AutoCloseable {

    //Instance variable
    private Connection con;

    //Constructor:
    public JokeOfTheDayRepo(Connection con) {
        this.con = con;
    }

    //Create method:
    public Optional<JokeOfTheDay> createJokeOfTheDay(JokeOfTheDay jod) throws SQLException {
        String sql = "INSERT INTO JokeOfTheDay (jokeId, totalVotes) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jod.getJokeId());
            ps.setInt(2, jod.getTotalVotes());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        jod.setJodId(rs.getInt(1));
                        return Optional.of(jod);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //Read current joke of the day method:
    public Optional<JokeOfTheDay> getJokeOfTheDay() throws SQLException {
        String sql = "SELECT * FROM JokeOfTheDay LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(JokeOfTheDay.builder()
                        .jodId(rs.getInt("jodId"))
                        .jokeId(rs.getInt("jokeId"))
                        .totalVotes(rs.getInt("totalVotes"))
                        .build());
            }
        }
        return Optional.empty();
    }

    //Read by ID method:
    public Optional<JokeOfTheDay> getJokeOfTheDayById(int jodId) throws SQLException {
        String sql = "SELECT * FROM JokeOfTheDay WHERE jodId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(JokeOfTheDay.builder()
                            .jodId(rs.getInt("jodId"))
                            .jokeId(rs.getInt("jokeId"))
                            .totalVotes(rs.getInt("totalVotes"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    //Update totalVotes method:
    public void updateTotalVotes(int jodId, int totalVotes) throws SQLException {
        String sql = "UPDATE JokeOfTheDay SET totalVotes = ? WHERE jodId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, totalVotes);
            ps.setInt(2, jodId);
            ps.executeUpdate();
        }
    }

    //Method to recalculate and refresh — finds highest voted approved joke
    //and replaces the current joke of the day entry:
    public Optional<JokeOfTheDay> refreshJokeOfTheDay() throws SQLException {
        String findSql = "SELECT j.jokeId, COUNT(v.voteId) AS voteCount " +
                "FROM Jokes j LEFT JOIN Votes v ON j.jokeId = v.jokeId " +
                "WHERE j.status = 'approved' " +
                "GROUP BY j.jokeId " +
                "ORDER BY voteCount DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(findSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int topJokeId = rs.getInt("jokeId");
                int topVotes = rs.getInt("voteCount");

                //Clear the old entry:
                String clearSql = "DELETE FROM JokeOfTheDay";
                try (PreparedStatement clearPs = con.prepareStatement(clearSql)) {
                    clearPs.executeUpdate();
                }

                //Insert the new winner:
                JokeOfTheDay jod = JokeOfTheDay.builder()
                        .jodId(0)
                        .jokeId(topJokeId)
                        .totalVotes(topVotes)
                        .build();

                return createJokeOfTheDay(jod);
            }
        }
        return Optional.empty();
    }

    //Delete by ID method:
    public boolean deleteJokeOfTheDayById(int jodId) throws SQLException {
        String sql = "DELETE FROM JokeOfTheDay WHERE jodId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jodId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void close() throws Exception {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}

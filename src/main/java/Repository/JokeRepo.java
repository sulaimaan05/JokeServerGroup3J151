package Repository;

import Model.Joke;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JokeRepo implements AutoCloseable {

    //Instance variable:
    private Connection con;

    //Constructor:
    public JokeRepo(Connection con) {
        this.con = con;
    }

    //Create method:
    public Optional<Joke> createJoke(Joke joke) throws SQLException {
        String sql = "INSERT INTO Jokes (creatorId, jokeText, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, joke.getCreatorId());
            ps.setString(2, joke.getJokeText());
            ps.setString(3, joke.getStatus());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        joke.setJokeId(rs.getInt(1));
                        return Optional.of(joke);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //Read by ID method:
    public Optional<Joke> getJokeById(int jokeId) throws SQLException {
        String sql = "SELECT * FROM Jokes WHERE jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jokeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Joke.builder()
                            .jokeId(rs.getInt("jokeId"))
                            .creatorId(rs.getInt("creatorId"))
                            .jokeText(rs.getString("jokeText"))
                            .status(rs.getString("status"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    //Read all approved jokes (for viewers) method:
    public List<Joke> getApprovedJokes() throws SQLException {
        List<Joke> jokeList = new ArrayList<>();
        String sql = "SELECT * FROM Jokes WHERE status = 'approved'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jokeList.add(Joke.builder()
                        .jokeId(rs.getInt("jokeId"))
                        .creatorId(rs.getInt("creatorId"))
                        .jokeText(rs.getString("jokeText"))
                        .status(rs.getString("status"))
                        .build());
            }
        }
        return jokeList;
    }

    //Read all pending jokes (for moderators) method:
    public List<Joke> getPendingJokes() throws SQLException {
        List<Joke> jokeList = new ArrayList<>();
        String sql = "SELECT * FROM Jokes WHERE status = 'pending'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jokeList.add(Joke.builder()
                        .jokeId(rs.getInt("jokeId"))
                        .creatorId(rs.getInt("creatorId"))
                        .jokeText(rs.getString("jokeText"))
                        .status(rs.getString("status"))
                        .build());
            }
        }
        return jokeList;
    }

    //Read all jokes by a specific creator method:
    public List<Joke> getJokesByCreator(int creatorId) throws SQLException {
        List<Joke> jokeList = new ArrayList<>();
        String sql = "SELECT * FROM Jokes WHERE creatorId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, creatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jokeList.add(Joke.builder()
                            .jokeId(rs.getInt("jokeId"))
                            .creatorId(rs.getInt("creatorId"))
                            .jokeText(rs.getString("jokeText"))
                            .status(rs.getString("status"))
                            .build());
                }
            }
        }
        return jokeList;
    }

    //Read all jokes method:
    public List<Joke> readAllJokes() throws SQLException {
        List<Joke> jokeList = new ArrayList<>();
        String sql = "SELECT * FROM Jokes";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jokeList.add(Joke.builder()
                        .jokeId(rs.getInt("jokeId"))
                        .creatorId(rs.getInt("creatorId"))
                        .jokeText(rs.getString("jokeText"))
                        .status(rs.getString("status"))
                        .build());
            }
        }
        return jokeList;
    }

    //Update full joke method:
    public void updateJoke(Joke joke) throws SQLException {
        String sql = "UPDATE Jokes SET creatorId=?, jokeText=?, status=? WHERE jokeId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, joke.getCreatorId());
            ps.setString(2, joke.getJokeText());
            ps.setString(3, joke.getStatus());
            ps.setInt(4, joke.getJokeId());
            ps.executeUpdate();
        }
    }

    //Update status only (used by moderators to approve/reject) method:
    public boolean updateJokeStatus(int jokeId, String status) throws SQLException {
        String sql = "UPDATE Jokes SET status = ? WHERE jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, jokeId);
            return ps.executeUpdate() > 0;
        }
    }

    //Delete method:
    public boolean deleteJokeById(int jokeId) throws SQLException {
        String sql = "DELETE FROM Jokes WHERE jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jokeId);
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
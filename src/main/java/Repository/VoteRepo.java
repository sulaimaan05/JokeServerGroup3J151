package Repository;

import Model.Vote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoteRepo implements AutoCloseable {

    private Connection con;

    public VoteRepo(Connection con) {
        this.con = con;
    }

    //Create method:
    public Optional<Vote> createVote(Vote vote) throws SQLException {
        String sql = "INSERT INTO Votes (userId, jokeId) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, vote.getUserId());
            ps.setInt(2, vote.getJokeId());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vote.setVoteId(rs.getInt(1));
                        return Optional.of(vote);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //Read by ID method:
    public Optional<Vote> getVoteById(int voteId) throws SQLException {
        String sql = "SELECT * FROM Votes WHERE voteId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Vote.builder()
                            .voteId(rs.getInt("voteId"))
                            .userId(rs.getInt("userId"))
                            .jokeId(rs.getInt("jokeId"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    //Check if a user has already voted on a joke:
    public boolean hasUserVoted(int userId, int jokeId) throws SQLException {
        String sql = "SELECT voteId FROM Votes WHERE userId = ? AND jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jokeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //Get all votes for a specific joke:
    public List<Vote> getVotesByJokeId(int jokeId) throws SQLException {
        List<Vote> voteList = new ArrayList<>();
        String sql = "SELECT * FROM Votes WHERE jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jokeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    voteList.add(Vote.builder()
                            .voteId(rs.getInt("voteId"))
                            .userId(rs.getInt("userId"))
                            .jokeId(rs.getInt("jokeId"))
                            .build());
                }
            }
        }
        return voteList;
    }

    //Get all votes by a specific user:
    public List<Vote> getVotesByUserId(int userId) throws SQLException {
        List<Vote> voteList = new ArrayList<>();
        String sql = "SELECT * FROM Votes WHERE userId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    voteList.add(Vote.builder()
                            .voteId(rs.getInt("voteId"))
                            .userId(rs.getInt("userId"))
                            .jokeId(rs.getInt("jokeId"))
                            .build());
                }
            }
        }
        return voteList;
    }

    //Count total votes for a joke:
    public int getVoteCountByJokeId(int jokeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Votes WHERE jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jokeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    //Read all votes:
    public List<Vote> readAllVotes() throws SQLException {
        List<Vote> voteList = new ArrayList<>();
        String sql = "SELECT * FROM Votes";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                voteList.add(Vote.builder()
                        .voteId(rs.getInt("voteId"))
                        .userId(rs.getInt("userId"))
                        .jokeId(rs.getInt("jokeId"))
                        .build());
            }
        }
        return voteList;
    }

    //Delete by voteId:
    public boolean deleteVoteById(int voteId) throws SQLException {
        String sql = "DELETE FROM Votes WHERE voteId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voteId);
            return ps.executeUpdate() > 0;
        }
    }

    //Delete by userId and jokeId (un-vote):
    public boolean deleteVoteByUserAndJoke(int userId, int jokeId) throws SQLException {
        String sql = "DELETE FROM Votes WHERE userId = ? AND jokeId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jokeId);
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

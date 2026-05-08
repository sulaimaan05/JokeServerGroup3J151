package Repository;

import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepo implements AutoCloseable {

    //Instance variable:
    private Connection con;

    //Constructor:
    public UserRepo(Connection con) {
        this.con = con;
    }

    //Create method:
    public Optional<User> createUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                        return Optional.of(user);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //Read by ID method:
    public Optional<User> getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(User.builder()
                            .userId(rs.getInt("userId"))
                            .username(rs.getString("username"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .role(rs.getString("role"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    //Read by username method:
    public Optional<User> getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(User.builder()
                            .userId(rs.getInt("userId"))
                            .username(rs.getString("username"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .role(rs.getString("role"))
                            .build());
                }
            }
        }
        return Optional.empty();
    }

    //Read all method:
    public List<User> readAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userList.add(User.builder()
                        .userId(rs.getInt("userId"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .build());
            }
        }
        return userList;
    }

    //Update method:
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET username=?, email=?, password=?, role=? WHERE userId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        }
    }

    //Delete method:
    public boolean deleteUserByUsername(String username) throws SQLException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
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
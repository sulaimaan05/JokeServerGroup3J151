package View;

import Model.Joke;
import Repository.DBConfig;
import Repository.JokeRepo;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ViewerScreen extends JFrame {

    //Instance variables:
    private JTextArea jokesArea;
    private JButton voteButton;
    private JButton jokeOfDayButton;
    private JButton logoutButton;
    private JLabel messageLabel;

    public ViewerScreen(String username) {
        setTitle("Joke Server - Welcome, " + username);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Title:
        JLabel titleLabel = new JLabel("Browse Jokes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //Jokes display area:
        //JScrollPane wraps the text area so it becomes scrollable.
        jokesArea = new JTextArea();
        jokesArea.setEditable(false); //User can read but not type in here
        jokesArea.setLineWrap(true);
        jokesArea.setWrapStyleWord(true);
        jokesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(jokesArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        //Bottom panel:
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        voteButton = new JButton("Vote on a Joke");
        logoutButton = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(voteButton);
        buttonPanel.add(logoutButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        //Load jokes when screen opens:
        loadJokes();

        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        voteButton.addActionListener(e -> showVoteDialog());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadJokes() {
        // TODO: call Controller/Service to get approved jokes
        // For now display placeholder text:
//        jokesArea.setText("Loading jokes...\n\n" +
//                "[1] Why don't scientists trust atoms?\n" +
//                "    Because they make up everything!\n\n" +
//                "[2] I told my wife she was drawing her eyebrows too high.\n" +
//                "    She looked surprised.\n");

        try {
            JokeRepo jokeRepo = new JokeRepo(DBConfig.getInstance().getConnection());
            List<Joke> jokes = jokeRepo.getApprovedJokes();

            StringBuilder sb = new StringBuilder();
            for (Joke joke : jokes) {
                sb.append("[").append(joke.getJokeId()).append("] ");
                sb.append(joke.getJokeText()).append("\n\n");
            }

            jokesArea.setText(sb.toString());
        } catch (SQLException e) {
            jokesArea.setText("Error loading jokes.");
            e.printStackTrace();
        }
    }

    private void showJokeOfDay() {
        // TODO: call Controller/Service to get joke of the day
        // JOptionPane is a quick built-in popup dialog
        JOptionPane.showMessageDialog(this,
                "Joke of the Day:\n\nWhy don't scientists trust atoms?\nBecause they make up everything!",
                "Joke of the Day",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showVoteDialog() {
        //Ask the user which joke ID they want to vote on:
        String input = JOptionPane.showInputDialog(this,
                "Enter the joke number you want to vote on:",
                "Vote on a Joke",
                JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            // TODO: call Controller/Service to cast vote
            messageLabel.setText("Vote cast on joke #" + input + "!");
        }
    }

    private void logout() {
        new LoginScreen().setVisible(true);
        dispose();
    }
}

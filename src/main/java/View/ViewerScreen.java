package View;

import Protocol.Protocol;

import javax.swing.*;
import java.awt.*;

public class ViewerScreen extends JFrame {

    private JTextArea jokesArea;
    private JButton voteButton;
    private JButton jokeOfDayButton;
    private JButton logoutButton;
    private JLabel messageLabel;

    public ViewerScreen() {
        setTitle("Joke Server - Welcome, " + LoginScreen.loggedInUserName);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Browse Jokes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        jokesArea = new JTextArea();
        jokesArea.setEditable(false);
        jokesArea.setLineWrap(true);
        jokesArea.setWrapStyleWord(true);
        jokesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(new JScrollPane(jokesArea), BorderLayout.CENTER);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        voteButton = new JButton("Vote on a Joke");
        logoutButton = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(voteButton);
        buttonPanel.add(logoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadJokes();

        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        voteButton.addActionListener(e -> showVoteDialog());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadJokes() {
        String response = LoginScreen.client.sendRequest(Protocol.buildGetApprovedJokes());

        if (Protocol.isSuccess(response)) {
            String data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1 && !parts[1].isBlank()) {
                String[] jokes = parts[1].split(Protocol.LIST_SEPARATOR);
                StringBuilder sb = new StringBuilder();
                for (String jokeStr : jokes) {
                    if (jokeStr.isBlank()) continue;
                    //Format: jokeId,creatorId,setup,punchline,status
                    String[] fields = jokeStr.split(",", 4);
                    if (fields.length >= 3) {
                        sb.append("[").append(fields[0]).append("] ");
                        sb.append(fields[2]).append("\n");
                        sb.append("    ").append(fields[3]).append("\n\n");
                    }
                }
                jokesArea.setText(sb.toString());
            } else {
                jokesArea.setText(parts[0]);
            }
        } else {
            jokesArea.setText("Could not load jokes: "
                    + Protocol.getData(response));
        }
    }

    private void showJokeOfDay() {
        String response = LoginScreen.client.sendRequest(Protocol.buildGetJod());

        String msg = Protocol.getData(response).split(Protocol.SEPARATOR)[0];
        JOptionPane.showMessageDialog(this, msg,
                "Joke of the Day",
                Protocol.isSuccess(response)
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.ERROR_MESSAGE);
    }

    private void showVoteDialog() {
        String jokeIdStr = JOptionPane.showInputDialog(this,
                "Enter the joke ID you want to vote on:",
                "Vote on a Joke",
                JOptionPane.QUESTION_MESSAGE);

        if (jokeIdStr == null || jokeIdStr.trim().isEmpty()) return;

        try {
            int jokeId = Integer.parseInt(jokeIdStr.trim());

            int choice = JOptionPane.showOptionDialog(this,
                    "How would you like to vote?",
                    "Vote",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Upvote", "Downvote"},
                    "Upvote");

            String response;
            if (choice == 0) {
                response = LoginScreen.client.sendRequest(Protocol.buildUpvote(LoginScreen.loggedInUserId, jokeId));
            } else {
                response = LoginScreen.client.sendRequest(Protocol.buildDownvote(LoginScreen.loggedInUserId, jokeId));
            }

            messageLabel.setForeground(
                    Protocol.isSuccess(response) ? Color.GREEN : Color.RED);
            messageLabel.setText(
                    Protocol.getData(response).split(Protocol.SEPARATOR)[0]);

        } catch (NumberFormatException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a valid joke ID number.");
        }
    }

    private void logout() {
        LoginScreen.loggedInUserId   = -1;
        LoginScreen.loggedInRole     = "";
        LoginScreen.loggedInUserName = "";
        new LoginScreen().setVisible(true);
        dispose();
    }
}
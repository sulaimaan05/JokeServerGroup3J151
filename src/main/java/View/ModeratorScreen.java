package View;

import Protocol.Protocol;

import javax.swing.*;
import java.awt.*;

public class ModeratorScreen extends JFrame {

    private JTextArea  pendingJokesArea;
    private JTextField jokeIdField;
    private JButton    approveButton;
    private JButton    rejectButton;
    private JButton    jokeOfDayButton;
    private JButton    refreshJodButton;
    private JButton    modRequestsButton;
    private JButton    logoutButton;
    private JLabel     messageLabel;

    public ModeratorScreen() {
        setTitle("Joke Server - Moderator: " + LoginScreen.loggedInUserName);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Moderator Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //Pending jokes display:
        JPanel pendingPanel = new JPanel(new BorderLayout(5, 5));
        pendingPanel.setBorder(BorderFactory.createTitledBorder("Pending Jokes"));
        pendingJokesArea = new JTextArea();
        pendingJokesArea.setEditable(false);
        pendingJokesArea.setLineWrap(true);
        pendingJokesArea.setWrapStyleWord(true);
        pendingPanel.add(new JScrollPane(pendingJokesArea), BorderLayout.CENTER);
        mainPanel.add(pendingPanel, BorderLayout.CENTER);

        //Review controls:
        JPanel reviewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        reviewPanel.setBorder(BorderFactory.createTitledBorder("Review a Joke"));
        reviewPanel.add(new JLabel("Joke ID:"));
        jokeIdField = new JTextField(5);
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        approveButton.setBackground(new Color(144, 238, 144));
        rejectButton.setBackground(new Color(255, 182, 193));
        reviewPanel.add(jokeIdField);
        reviewPanel.add(approveButton);
        reviewPanel.add(rejectButton);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        refreshJodButton = new JButton("Refresh JOD");
        modRequestsButton = new JButton("Mod Requests");
        logoutButton = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(refreshJodButton);
        buttonPanel.add(modRequestsButton);
        buttonPanel.add(logoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(reviewPanel,  BorderLayout.NORTH);
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel,  BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadPendingJokes();

        approveButton.addActionListener(e -> handleDecision("approve"));
        rejectButton.addActionListener(e -> handleDecision("reject"));
        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        refreshJodButton.addActionListener(e -> handleRefreshJod());
        modRequestsButton.addActionListener(e -> showModRequests());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadPendingJokes() {
        String response = LoginScreen.client.sendRequest(Protocol.buildGetPending(LoginScreen.loggedInRole));

        if (Protocol.isSuccess(response)) {
            String data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1 && !parts[1].isBlank()) {
                String[]      jokes = parts[1].split(Protocol.LIST_SEPARATOR);
                StringBuilder sb    = new StringBuilder();
                for (String jokeStr : jokes) {
                    if (jokeStr.isBlank()) continue;
                    // format: jokeId,creatorId,setup,punchline,status
                    String[] fields = jokeStr.split(",", 5);
                    if (fields.length >= 4) {
                        sb.append("[ID: ").append(fields[0]).append("] ");
                        sb.append(fields[2]).append("\n");
                        sb.append("    ").append(fields[3]).append("\n\n");
                    }
                }
                pendingJokesArea.setText(sb.toString());
            } else {
                pendingJokesArea.setText(parts[0]);
            }
        } else {
            pendingJokesArea.setText("Could not load pending jokes: "
                    + Protocol.getData(response));
        }
    }

    private void handleDecision(String decision) {
        String input = jokeIdField.getText().trim();

        if (input.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a joke ID.");
            return;
        }

        try {
            int jokeId = Integer.parseInt(input);
            String response;

            if (decision.equals("approve")) {
                response = LoginScreen.client.sendRequest(
                        Protocol.buildApproveJoke(
                                LoginScreen.loggedInRole, jokeId));
            } else {
                response = LoginScreen.client.sendRequest(
                        Protocol.buildRejectJoke(
                                LoginScreen.loggedInRole, jokeId));
            }

            messageLabel.setForeground(
                    Protocol.isSuccess(response) ? Color.GREEN : Color.RED);
            messageLabel.setText(
                    Protocol.getData(response).split(Protocol.SEPARATOR)[0]);

            if (Protocol.isSuccess(response)) {
                jokeIdField.setText("");
                loadPendingJokes();
            }

        } catch (NumberFormatException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a valid joke ID number.");
        }
    }

    private void showJokeOfDay() {
        String response = LoginScreen.client.sendRequest(
                Protocol.buildGetJod());
        String msg = Protocol.getData(response).split(Protocol.SEPARATOR)[0];
        JOptionPane.showMessageDialog(this, msg,
                "Joke of the Day",
                Protocol.isSuccess(response)
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.ERROR_MESSAGE);
    }

    private void handleRefreshJod() {
        String response = LoginScreen.client.sendRequest(
                Protocol.buildRefreshJod(LoginScreen.loggedInRole));

        messageLabel.setForeground(
                Protocol.isSuccess(response) ? Color.GREEN : Color.RED);
        messageLabel.setText(
                Protocol.getData(response).split(Protocol.SEPARATOR)[0]);
    }

    private void showModRequests() {
        String response = LoginScreen.client.sendRequest(Protocol.buildGetModRequests(LoginScreen.loggedInRole));

        if (Protocol.isSuccess(response)) {
            String data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1 && !parts[1].isBlank()) {
                String[]      users = parts[1].split(Protocol.LIST_SEPARATOR);
                StringBuilder sb    = new StringBuilder(
                        "Pending Moderator Requests:\n\n");
                for (String userStr : users) {
                    if (userStr.isBlank()) continue;
                    // format: userId,username,email,role,displayName
                    String[] fields = userStr.split(",", 5);
                    if (fields.length >= 2) {
                        sb.append("[ID: ").append(fields[0]).append("] ");
                        sb.append(fields[1]).append("\n");
                    }
                }

                String input = JOptionPane.showInputDialog(this,
                        sb + "\nEnter User ID to approve or deny:",
                        "Moderator Requests",
                        JOptionPane.PLAIN_MESSAGE);

                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int targetId = Integer.parseInt(input.trim());
                        int choice   = JOptionPane.showOptionDialog(this,
                                "What would you like to do with user #"
                                        + targetId + "?",
                                "Moderator Request",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new String[]{"Approve", "Deny"},
                                "Approve");

                        String actionResponse;
                        if (choice == 0) {
                            actionResponse = LoginScreen.client.sendRequest(
                                    Protocol.buildApproveModRequest(
                                            LoginScreen.loggedInRole,
                                            targetId));
                        } else {
                            actionResponse = LoginScreen.client.sendRequest(
                                    Protocol.buildDenyModRequest(
                                            LoginScreen.loggedInRole,
                                            targetId));
                        }

                        messageLabel.setForeground(
                                Protocol.isSuccess(actionResponse)
                                        ? Color.GREEN : Color.RED);
                        messageLabel.setText(
                                Protocol.getData(actionResponse)
                                        .split(Protocol.SEPARATOR)[0]);

                    } catch (NumberFormatException e) {
                        messageLabel.setForeground(Color.RED);
                        messageLabel.setText("Invalid user ID.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, parts[0],
                        "Moderator Requests",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    Protocol.getData(response),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
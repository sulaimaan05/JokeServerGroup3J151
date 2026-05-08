package View;

import javax.swing.*;
import java.awt.*;

public class ModeratorScreen extends JFrame {

    //Instance variables:
    private JTextArea pendingJokesArea;
    private JTextField jokeIdField;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton jokeOfDayButton;
    private JButton logoutButton;
    private JLabel messageLabel;

    public ModeratorScreen(String username) {
        setTitle("Joke Server - Moderator: " + username);
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Title:
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

        //Bottom - review controls:
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        JPanel reviewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        reviewPanel.setBorder(BorderFactory.createTitledBorder("Review a Joke"));
        reviewPanel.add(new JLabel("Joke ID:"));
        jokeIdField  = new JTextField(5);
        approveButton = new JButton("Approve");
        rejectButton  = new JButton("Reject");
        approveButton.setBackground(new Color(144, 238, 144)); // light green
        rejectButton.setBackground(new Color(255, 182, 193));  // light red
        reviewPanel.add(jokeIdField);
        reviewPanel.add(approveButton);
        reviewPanel.add(rejectButton);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        logoutButton    = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(logoutButton);

        bottomPanel.add(reviewPanel, BorderLayout.NORTH);
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadPendingJokes();

        approveButton.addActionListener(e -> handleDecision("approved"));
        rejectButton.addActionListener(e -> handleDecision("rejected"));
        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadPendingJokes() {
        // TODO: call Controller/Service to get pending jokes
        pendingJokesArea.setText(
                "[ID: 2] I am reading a book about anti-gravity.\n" +
                        "        It is impossible to put down.\n\n" +
                        "[ID: 3] I used to hate facial hair.\n" +
                        "        But then it grew on me.\n");
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
            // TODO: call Controller/Service to update joke status
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Joke #" + jokeId + " has been " + decision + ".");
            jokeIdField.setText("");
            loadPendingJokes();
        } catch (NumberFormatException ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter a valid joke ID number.");
        }
    }

    private void showJokeOfDay() {
        JOptionPane.showMessageDialog(this,
                "Joke of the Day:\n\nWhy don't scientists trust atoms?\nBecause they make up everything!",
                "Joke of the Day",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        new LoginScreen().setVisible(true);
        dispose();
    }
}

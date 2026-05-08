package View;

import javax.swing.*;
import java.awt.*;

public class CreatorScreen extends JFrame {

    //Instance variables:
    private JTextArea jokeInputArea;
    private JTextArea myJokesArea;
    private JButton submitButton;
    private JButton jokeOfDayButton;
    private JButton logoutButton;
    private JLabel messageLabel;

    public CreatorScreen(String username) {
        setTitle("Joke Server - Creator: " + username);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Title:
        JLabel titleLabel = new JLabel("Creator Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //Center - split into submit area and my jokes area:
        JPanel centrePanel = new JPanel(new GridLayout(2, 1, 10, 10));

        //Submit joke section:
        JPanel submitPanel = new JPanel(new BorderLayout(5, 5));
        submitPanel.setBorder(BorderFactory.createTitledBorder("Submit a New Joke"));
        jokeInputArea = new JTextArea(4, 30);
        jokeInputArea.setLineWrap(true);
        jokeInputArea.setWrapStyleWord(true);
        submitPanel.add(new JScrollPane(jokeInputArea), BorderLayout.CENTER);
        submitButton = new JButton("Submit Joke");
        submitPanel.add(submitButton, BorderLayout.SOUTH);
        centrePanel.add(submitPanel);

        //My jokes section:
        JPanel myJokesPanel = new JPanel(new BorderLayout(5, 5));
        myJokesPanel.setBorder(BorderFactory.createTitledBorder("My Jokes"));
        myJokesArea = new JTextArea();
        myJokesArea.setEditable(false);
        myJokesArea.setLineWrap(true);
        myJokesArea.setWrapStyleWord(true);
        myJokesPanel.add(new JScrollPane(myJokesArea), BorderLayout.CENTER);
        centrePanel.add(myJokesPanel);

        mainPanel.add(centrePanel, BorderLayout.CENTER);

        //Bottom:
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        logoutButton    = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(logoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadMyJokes();

        submitButton.addActionListener(e -> handleSubmit());
        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        logoutButton.addActionListener(e -> logout());
    }

    private void handleSubmit() {
        String jokeText = jokeInputArea.getText().trim();

        if (jokeText.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please write a joke first.");
            return;
        }

        // TODO: call Controller/Service to submit joke
        messageLabel.setForeground(Color.GREEN);
        messageLabel.setText("Joke submitted for review!");
        jokeInputArea.setText("");
        loadMyJokes();
    }

    private void loadMyJokes() {
        // TODO: call Controller/Service to get this creator's jokes
        myJokesArea.setText("[1] Why don't scientists trust atoms? (approved)\n\n" +
                "[2] I am reading a book about anti-gravity. (pending)\n");
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

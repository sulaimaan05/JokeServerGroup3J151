package View;

import Protocol.Protocol;

import javax.swing.*;
import java.awt.*;

public class CreatorScreen extends JFrame {

    private JTextArea jokeTextArea;
    private JTextField categoryField;
    private JTextArea myJokesArea;
    private JButton submitButton;
    private JButton jokeOfDayButton;
    private JButton logoutButton;
    private JLabel messageLabel;

    public CreatorScreen() {
        setTitle("Joke Server - Creator: " + LoginScreen.loggedInUserName);
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Creator Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centrePanel = new JPanel(new GridLayout(2, 1, 10, 10));

        //Submit section:
        JPanel submitPanel = new JPanel(new BorderLayout(5, 5));
        submitPanel.setBorder(BorderFactory.createTitledBorder("Submit a New Joke"));

        JPanel inputFields = new JPanel(new GridLayout(6, 1, 5, 5));

        inputFields.add(new JLabel("Setup:"));
        jokeTextArea = new JTextArea(2, 30);
        jokeTextArea.setLineWrap(true);
        inputFields.add(new JScrollPane(jokeTextArea));

        inputFields.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputFields.add(categoryField);

        submitPanel.add(inputFields, BorderLayout.CENTER);
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

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLUE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        jokeOfDayButton = new JButton("Joke of the Day");
        logoutButton = new JButton("Logout");
        buttonPanel.add(jokeOfDayButton);
        buttonPanel.add(logoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel,  BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadMyJokes();

        submitButton.addActionListener(e -> handleSubmit());
        jokeOfDayButton.addActionListener(e -> showJokeOfDay());
        logoutButton.addActionListener(e -> logout());
    }

    private void handleSubmit() {
        String jokeText = jokeTextArea.getText().trim();

        if (jokeText.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please write a joke first.");
            return;
        }

        String response = LoginScreen.client.sendRequest(
                Protocol.buildSubmitJoke(LoginScreen.loggedInUserId, LoginScreen.loggedInRole, jokeText));

        messageLabel.setForeground(Protocol.isSuccess(response) ? Color.GREEN : Color.RED);
        messageLabel.setText(Protocol.getData(response).split(Protocol.SEPARATOR)[0]);

        if (Protocol.isSuccess(response)) {
            jokeTextArea.setText("");
            loadMyJokes();
        }
    }

    private void loadMyJokes() {
        String response = LoginScreen.client.sendRequest(
                Protocol.buildGetMyJokes(
                        LoginScreen.loggedInUserId,
                        LoginScreen.loggedInRole));

        if (Protocol.isSuccess(response)) {
            String data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1 && !parts[1].isBlank()) {
                String[] jokes = parts[1].split(Protocol.LIST_SEPARATOR);
                StringBuilder sb = new StringBuilder();
                for (String jokeStr : jokes) {
                    if (jokeStr.isBlank()) continue;
                    //Format: jokeId,creatorId,setup,punchline,status.
                    // CORRECT
                    String[] fields = jokeStr.split(",", 4);
                    if (fields.length >= 4) {
                        sb.append("[").append(fields[0]).append("] ");
                        sb.append(fields[2]).append("\n");  // jokeText
                        sb.append("    Status: ").append(fields[3]).append("\n\n");
                    }
                }
                myJokesArea.setText(sb.toString());
            } else {
                myJokesArea.setText(parts[0]);
            }
        } else {
            myJokesArea.setText("Could not load jokes: "
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

    private void logout() {
        LoginScreen.loggedInUserId   = -1;
        LoginScreen.loggedInRole     = "";
        LoginScreen.loggedInUserName = "";
        new LoginScreen().setVisible(true);
        dispose();
    }
}
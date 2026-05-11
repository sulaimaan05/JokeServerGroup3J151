package View;

import Client.Client;
import Protocol.Protocol;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    //Shared client — one connection for the whole app:
    public static final Client client = new Client();

    //Logged-in user details — set once at login, used by all screens afterwards:
    public static int loggedInUserId   = -1;
    public static String loggedInRole     = "";
    public static String loggedInUserName = "";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    public LoginScreen() {
        setTitle("Joke Server - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Joke Server", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        //Connect to the server when the application starts:
        if (!client.connect()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to server.\n" +
                            "Make sure the server is running.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterScreen());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        //Send login request to server:
        String response = LoginScreen.client.sendRequest(
                Protocol.buildLogin(username, password));

        if (Protocol.isSuccess(response)) {
            //Response format:
            //SUCCESS|Welcome back, alice!|1,alice,alice@test.com,creator,Alice
            String data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1) {
                //Format: userId,username,email,role,displayName
                String[] userFields = parts[1].split(",", 5);
                LoginScreen.loggedInUserId   = Integer.parseInt(userFields[0]);
                LoginScreen.loggedInUserName = userFields[1];
                LoginScreen.loggedInRole     = userFields[3];
            }

            //Open the correct screen based on role.
            //NOTE: must match exact role strings from your Controllers.
            switch (LoginScreen.loggedInRole) {
                case "viewer":
                    new ViewerScreen().setVisible(true);
                    break;
                case "creator":
                    new CreatorScreen().setVisible(true);
                    break;
                case "moderator":
                    new ModeratorScreen().setVisible(true);
                    break;
                default:
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Unknown role: " + LoginScreen.loggedInRole);
                    return;
            }
            dispose();

        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(Protocol.getData(response));
        }
    }

    private void openRegisterScreen() {
        new RegisterScreen().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
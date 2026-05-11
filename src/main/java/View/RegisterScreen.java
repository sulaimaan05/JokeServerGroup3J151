package View;

import Protocol.Protocol;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;

    public RegisterScreen() {
        setTitle("Joke Server - Register");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Account Type:"));
        //Role names must match exactly what the server expects:
        String[] roles = {"viewer", "joke_creator", "moderator"};
        roleComboBox   = new JComboBox<>(roles);
        formPanel.add(roleComboBox);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> goBack());
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // Send register request to server
        String response = LoginScreen.client.sendRequest(Protocol.buildRegister(username, password, email, role));

        if (Protocol.isSuccess(response)) {
            //Same parsing as login — server returns a User object:
            String   data  = Protocol.getData(response);
            String[] parts = data.split(Protocol.SEPARATOR, 2);

            if (parts.length > 1) {
                //Format: userId,username,email,role,displayName
                String[] userFields = parts[1].split(",", 5);
                LoginScreen.loggedInUserId   = Integer.parseInt(userFields[0]);
                LoginScreen.loggedInUserName = userFields[1];
                LoginScreen.loggedInRole     = userFields[3];
            }

            //Open the correct screen straight away — no need to go back to login after registering.
            switch (LoginScreen.loggedInRole) {
                case "viewer":
                    new ViewerScreen().setVisible(true);
                    break;
                case "joke_creator":
                    new CreatorScreen().setVisible(true);
                    break;
                case "moderator":
                    new ModeratorScreen().setVisible(true);
                    break;
                default:
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Unknown role: "
                            + LoginScreen.loggedInRole);
                    return;
            }
            dispose();

        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(Protocol.getData(response));
        }
    }

    private void goBack() {
        new LoginScreen().setVisible(true);
        dispose();
    }
}
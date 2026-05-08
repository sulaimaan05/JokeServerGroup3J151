package View;

import Model.User;
import Repository.DBConfig;
import Repository.UserRepo;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;

public class LoginScreen extends JFrame {

    //Declare components as instance variables
    //so all methods in the class can access them
    private JTextField usernameField; //JTextField — a single line input box where the user types.
    private JPasswordField passwordField; //JPasswordField — same as JTextField but hides the characters.
    private JButton loginButton; //JButton — a clickable button.
    private JButton registerButton; //JButton — a clickable button.
    private JLabel messageLabel; //JLabel — displays text the user can't edit, like a heading or description.

    public LoginScreen() {
        //Window setup:
        setTitle("Joke Server - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //Centres the window on screen.
        setResizable(false);

        //Main panel:
        //BorderLayout divides the panel into 5 zones:
        //North (top), South (bottom), East, West, Center
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Title at the top:
        JLabel titleLabel = new JLabel("Joke Server", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        //Form in the centre:
        //GridLayout(rows, cols, hgap, vgap) arranges components in a grid.
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        //messageLabel shows success or error messages:
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        //Buttons at the bottom:
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //Add main panel to frame:
        add(mainPanel);

        //Button actions:
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterScreen());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        //Basic validation before sending anything:
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // TODO: This is where Member 2's Controller/Service gets called
        //For now we just simulate a successful login for testing
        //Replace this block when the Controller is ready:
//        messageLabel.setForeground(Color.GREEN);
//        messageLabel.setText("Login successful!");

        try {
            UserRepo userRepo = new UserRepo(DBConfig.getInstance().getConnection());
            Optional<User> result = userRepo.getUserByUsername(username);

            if (result.isPresent()) {
                User user = result.get();

                //Check whether the password matches:
                if (!user.getPassword().equals(password)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Incorrect password.");
                    return;
                }

                //Open the correct screen based on role:
                switch (user.getRole()) {
                    case "viewer":
                        new ViewerScreen(username).setVisible(true);
                        break;

                    case "creator":
                        new CreatorScreen(username).setVisible(true);
                        break;

                    case "moderator":
                        new ModeratorScreen(username).setVisible(true);
                        break;
                }
                dispose();
            }else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("User not found.");
            }
        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Database error.");
            e.printStackTrace();
        }

        //Once login works, open the correct screen based on role
        //Example: openViewerScreen();
    }

    private void openRegisterScreen() {
        new RegisterScreen().setVisible(true);
        dispose(); //Closes this window.
    }

    //Call this to launch the login screen:
    public static void main(String[] args) {
        //SwingUtilities.invokeLater ensures the UI runs on
        //the correct thread -- always do this for Swing apps.
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}

package Server;

import Controller.*;
import Repository.*;
import Service.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server {

    private static final int PORT = 5000;

    private ServerSocket serverSocket;

    //All controllers instantiated once and shared across all client handler threads:
    private final AuthController authController;
    private final UserController userController;
    private final JokeController jokeController;
    private final ModerationController moderationController;
    private final VoteController voteController;
    private final JokeOfTheDayController jodController;

    public Server() {
        //Instantiate repositories:
        Connection con = DBConfig.getInstance().getConnection();

        UserRepo userRepo = new UserRepo(con);
        JokeRepo jokeRepo = new JokeRepo(con);
        VoteRepo voteRepo = new VoteRepo(con);
        JokeOfTheDayRepo jodRepo = new JokeOfTheDayRepo(con);

        //Instantiate services with their repositories:
        AuthService authService  = new AuthService(userRepo);
        UserService userService  = new UserService(userRepo);
        JokeService jokeService  = new JokeService(jokeRepo);
        ModerationService modService   = new ModerationService(jokeRepo, userRepo);
        VoteService voteService  = new VoteService(voteRepo, jokeRepo);
        JokeOfTheDayService jodService   = new JokeOfTheDayService(jodRepo);

        //Instantiate controllers with their services:
        this.authController = new AuthController(authService);
        this.userController = new UserController(userService);
        this.jokeController = new JokeController(jokeService);
        this.moderationController = new ModerationController(modService);
        this.voteController = new VoteController(voteService);
        this.jodController = new JokeOfTheDayController(jodService);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Joke Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                //Waits for a client to connect:
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: "
                        + clientSocket.getInetAddress().getHostAddress());

                //Gives each client their own handler thread so multiple clients can be served at the same time:
                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        authController,
                        userController,
                        jokeController,
                        moderationController,
                        voteController,
                        jodController
                );
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}

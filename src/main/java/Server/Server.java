package Server;

import Controller.*;
import Repository.DBConfig;
import Service.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5000;

    private ServerSocket serverSocket;

    //All controllers instantiated once and shared across all client handler threads:
    private final AuthController        authController;
    private final UserController        userController;
    private final JokeController        jokeController;
    private final ModerationController  moderationController;
    private final VoteController        voteController;
    private final JokeOfTheDayController jodController;

    public Server() {
        //Instantiate services:
        AuthService         authService      = new AuthService(
                DBConfig.getInstance().getConnection());
        UserService         userService      = new UserService(
                DBConfig.getInstance().getConnection());
        JokeService         jokeService      = new JokeService(
                DBConfig.getInstance().getConnection());
        ModerationService   modService       = new ModerationService(
                DBConfig.getInstance().getConnection());
        VoteService         voteService      = new VoteService(
                DBConfig.getInstance().getConnection());
        JokeOfTheDayService jodService       = new JokeOfTheDayService(
                DBConfig.getInstance().getConnection());

        //Instantiate controllers with their services:
        this.authController       = new AuthController(authService);
        this.userController       = new UserController(userService);
        this.jokeController       = new JokeController(jokeService);
        this.moderationController = new ModerationController(modService);
        this.voteController       = new VoteController(voteService);
        this.jodController        = new JokeOfTheDayController(jodService);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Joke Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                //Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: "
                        + clientSocket.getInetAddress().getHostAddress());

                //Give each client their own handler thread so multiple clients can be served at the same time
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

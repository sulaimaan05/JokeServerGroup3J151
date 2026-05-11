package Server;

import Controller.*;
import Model.*;
import Protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket                 clientSocket;
    private final AuthController         authController;
    private final UserController         userController;
    private final JokeController         jokeController;
    private final ModerationController   moderationController;
    private final VoteController         voteController;
    private final JokeOfTheDayController jodController;

    public ClientHandler(Socket clientSocket,
                         AuthController authController,
                         UserController userController,
                         JokeController jokeController,
                         ModerationController moderationController,
                         VoteController voteController,
                         JokeOfTheDayController jodController) {
        this.clientSocket        = clientSocket;
        this.authController      = authController;
        this.userController      = userController;
        this.jokeController      = jokeController;
        this.moderationController= moderationController;
        this.voteController      = voteController;
        this.jodController       = jodController;
    }

    @Override
    public void run() {
        try (
                BufferedReader in  = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter    out = new PrintWriter(
                        clientSocket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("[Server] Received: " + request);
                String response = handleRequest(request);
                System.out.println("[Server] Sending:  " + response);
                out.println(response);
            }

        } catch (IOException e) {
            System.err.println("ClientHandler IO error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Routes the request to the correct controller and serializes the response back to a string:
    private String handleRequest(String request) {
        if (request == null || request.isBlank())
            return Protocol.error("Empty request.");

        String[] parts  = request.split(Protocol.SEPARATOR);
        String   action = parts[0].toUpperCase();

        try {
            switch (action) {

                //AUTH:
                case Protocol.REGISTER: {
                    //REGISTER|username|password|email|role
                    if (parts.length < 5)
                        return Protocol.error("Missing fields for REGISTER.");
                    ControllerResponse<User> res = authController.register(
                            parts[1], parts[2], parts[3], parts[4]);
                    return serialize(res, res.isSuccess() ? serializeUser(res.getData()) : null);
                }

                case Protocol.LOGIN: {
                    //LOGIN|username|password
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for LOGIN.");
                    ControllerResponse<User> res = authController.login(
                            parts[1], parts[2]);
                    return serialize(res, res.isSuccess() ? serializeUser(res.getData()) : null);
                }

                //USER:
                case Protocol.GET_USER_BY_ID: {
                    //GET_USER_BY_ID|userId
                    if (parts.length < 2)
                        return Protocol.error("Missing userId.");
                    ControllerResponse<User> res = userController.getUserById(
                            Integer.parseInt(parts[1]));
                    return serialize(res, res.isSuccess() ? serializeUser(res.getData()) : null);
                }

                case Protocol.GET_ALL_USERS: {
                    //GET_ALL_USERS|requesterRole
                    if (parts.length < 2)
                        return Protocol.error("Missing requesterRole.");
                    ControllerResponse<List<User>> res = userController.getAllUsers(parts[1]);
                    return serialize(res, res.isSuccess() ? serializeUserList(res.getData()) : null);
                }

                case Protocol.UPDATE_EMAIL: {
                    //UPDATE_EMAIL|userId|newEmail
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPDATE_EMAIL.");
                    ControllerResponse<Void> res = userController.updateEmail(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, null);
                }

                case Protocol.UPDATE_PASSWORD: {
                    //UPDATE_PASSWORD|userId|newPassword
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPDATE_PASSWORD.");
                    ControllerResponse<Void> res = userController.updatePassword(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, null);
                }

                case Protocol.UPDATE_USERNAME: {
                    //UPDATE_USERNAME|userId|newUsername
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPDATE_USERNAME.");
                    ControllerResponse<Void> res = userController.updateUsername(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, null);
                }

                case Protocol.UPGRADE_ROLE: {
                    //UPGRADE_ROLE|userId|newRole
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPGRADE_ROLE.");
                    ControllerResponse<Void> res = userController.upgradeRole(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, null);
                }

                case Protocol.DOWNGRADE_ROLE: {
                    //DOWNGRADE_ROLE|userId|newRole
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for DOWNGRADE_ROLE.");
                    ControllerResponse<Void> res = userController.downgradeRole(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, null);
                }

                case Protocol.DELETE_ACCOUNT: {
                    //DELETE_ACCOUNT|username
                    if (parts.length < 2)
                        return Protocol.error("Missing username.");
                    ControllerResponse<Void> res = userController.deleteAccount(parts[1]);
                    return serialize(res, null);
                }

                //JOKES:
                case Protocol.SUBMIT_JOKE: {
                    //SUBMIT_JOKE|creatorId|creatorRole|setup|punchline|category
                    if (parts.length < 6)
                        return Protocol.error("Missing fields for SUBMIT_JOKE.");
                    ControllerResponse<Joke> res = jokeController.submitJoke(
                            Integer.parseInt(parts[1]), parts[2],
                            parts[3]);
                    return serialize(res, res.isSuccess() ? serializeJoke(res.getData()) : null);
                }

                case Protocol.GET_APPROVED: {
                    //GET_APPROVED
                    ControllerResponse<List<Joke>> res = jokeController.getApprovedJokes();
                    return serialize(res, res.isSuccess() ? serializeJokeList(res.getData()) : null);
                }

                case Protocol.GET_MY_JOKES: {
                    //GET_MY_JOKES|creatorId|creatorRole
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for GET_MY_JOKES.");
                    ControllerResponse<List<Joke>> res = jokeController.getMyJokes(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, res.isSuccess() ? serializeJokeList(res.getData()) : null);
                }

                case Protocol.GET_ALL_JOKES: {
                    //GET_ALL_JOKES|requesterRole
                    if (parts.length < 2)
                        return Protocol.error("Missing requesterRole.");
                    ControllerResponse<List<Joke>> res = jokeController.getAllJokes(parts[1]);
                    return serialize(res, res.isSuccess() ? serializeJokeList(res.getData()) : null);
                }

                case Protocol.GET_JOKE_BY_ID: {
                    //GET_JOKE_BY_ID|jokeId|requesterRole
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for GET_JOKE_BY_ID.");
                    ControllerResponse<Joke> res = jokeController.getJokeById(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, res.isSuccess() ? serializeJoke(res.getData()) : null);
                }

                case Protocol.EDIT_JOKE: {
                    //EDIT_JOKE|requesterId|jokeId|newSetup|newPunchline|newCategory
                    if (parts.length < 6)
                        return Protocol.error("Missing fields for EDIT_JOKE.");
                    ControllerResponse<Void> res = jokeController.editJoke(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                            parts[3]);
                    return serialize(res, null);
                }

                case Protocol.DELETE_JOKE: {
                    //DELETE_JOKE|requesterId|requesterRole|jokeId
                    if (parts.length < 4)
                        return Protocol.error("Missing fields for DELETE_JOKE.");
                    ControllerResponse<Void> res = jokeController.deleteJoke(
                            Integer.parseInt(parts[1]), parts[2],
                            Integer.parseInt(parts[3]));
                    return serialize(res, null);
                }

                //MODERATION:
                case Protocol.GET_PENDING: {
                    //GET_PENDING|moderatorRole
                    if (parts.length < 2)
                        return Protocol.error("Missing moderatorRole.");
                    ControllerResponse<List<Joke>> res = moderationController
                            .getPendingJokes(parts[1]);
                    return serialize(res, res.isSuccess() ? serializeJokeList(res.getData()) : null);
                }

                case Protocol.APPROVE_JOKE: {
                    //APPROVE_JOKE|moderatorRole|jokeId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for APPROVE_JOKE.");
                    ControllerResponse<Void> res = moderationController.approveJoke(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                case Protocol.REJECT_JOKE: {
                    //REJECT_JOKE|moderatorRole|jokeId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for REJECT_JOKE.");
                    ControllerResponse<Void> res = moderationController.rejectJoke(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                case Protocol.GET_MOD_REQUESTS: {
                    //GET_MOD_REQUESTS|moderatorRole
                    if (parts.length < 2)
                        return Protocol.error("Missing moderatorRole.");
                    ControllerResponse<List<User>> res = moderationController
                            .getPendingModeratorRequests(parts[1]);
                    return serialize(res, res.isSuccess() ? serializeUserList(res.getData()) : null);
                }

                case Protocol.APPROVE_MOD_REQUEST: {
                    //APPROVE_MOD_REQUEST|moderatorRole|targetUserId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for APPROVE_MOD_REQUEST.");
                    ControllerResponse<Void> res = moderationController.approveModeratorRequest(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                case Protocol.DENY_MOD_REQUEST: {
                    //DENY_MOD_REQUEST|moderatorRole|targetUserId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for DENY_MOD_REQUEST.");
                    ControllerResponse<Void> res = moderationController.denyModeratorRequest(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                //VOTES:
                case Protocol.UPVOTE: {
                    //UPVOTE|userId|jokeId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPVOTE.");
                    ControllerResponse<Vote> res = voteController.upvote(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    return serialize(res, res.isSuccess() ? serializeVote(res.getData()) : null);
                }

                case Protocol.DOWNVOTE: {
                    //DOWNVOTE|userId|jokeId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for DOWNVOTE.");
                    ControllerResponse<Vote> res = voteController.downvote(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    return serialize(res, res.isSuccess() ? serializeVote(res.getData()) : null);
                }

                case Protocol.RETRACT_VOTE: {
                    //RETRACT_VOTE|userId|jokeId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for RETRACT_VOTE.");
                    ControllerResponse<Void> res = voteController.retractVote(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                case Protocol.GET_VOTE_COUNT: {
                    //GET_VOTE_COUNT|jokeId
                    if (parts.length < 2)
                        return Protocol.error("Missing jokeId.");
                    ControllerResponse<Integer> res = voteController.getVoteCount(
                            Integer.parseInt(parts[1]));
                    return serialize(res, res.isSuccess() ? String.valueOf(res.getData()) : null);
                }

                case Protocol.GET_VOTES_JOKE: {
                    //GET_VOTES_JOKE|jokeId|requesterRole
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for GET_VOTES_JOKE.");
                    ControllerResponse<List<Vote>> res = voteController.getVotesForJoke(
                            Integer.parseInt(parts[1]), parts[2]);
                    return serialize(res, res.isSuccess() ? serializeVoteList(res.getData()) : null);
                }

                case Protocol.GET_VOTES_USER: {
                    //GET_VOTES_USER|requesterId|requesterRole|targetUserId
                    if (parts.length < 4)
                        return Protocol.error("Missing fields for GET_VOTES_USER.");
                    ControllerResponse<List<Vote>> res = voteController.getVotesByUser(
                            Integer.parseInt(parts[1]), parts[2],
                            Integer.parseInt(parts[3]));
                    return serialize(res, res.isSuccess() ? serializeVoteList(res.getData()) : null);
                }

                case Protocol.DELETE_VOTE: {
                    //DELETE_VOTE|requesterRole|voteId
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for DELETE_VOTE.");
                    ControllerResponse<Void> res = voteController.deleteVote(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                //JOKE OF THE DAY:
                case Protocol.GET_JOD: {
                    //GET_JOD
                    ControllerResponse<JokeOfTheDay> res = jodController.getJokeOfTheDay();
                    return serialize(res, res.isSuccess() ? serializeJod(res.getData()) : null);
                }

                case Protocol.GET_JOD_BY_ID: {
                    //GET_JOD_BY_ID|id
                    if (parts.length < 2)
                        return Protocol.error("Missing id.");
                    ControllerResponse<JokeOfTheDay> res = jodController.getJokeOfTheDayById(
                            Integer.parseInt(parts[1]));
                    return serialize(res, res.isSuccess() ? serializeJod(res.getData()) : null);
                }

                case Protocol.REFRESH_JOD: {
                    //REFRESH_JOD|requesterRole
                    if (parts.length < 2)
                        return Protocol.error("Missing requesterRole.");
                    ControllerResponse<JokeOfTheDay> res = jodController.refreshJokeOfTheDay(
                            parts[1]);
                    return serialize(res, res.isSuccess() ? serializeJod(res.getData()) : null);
                }

                case Protocol.UPDATE_JOD_VOTES: {
                    //UPDATE_JOD_VOTES|jodId|totalVotes
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for UPDATE_JOD_VOTES.");
                    ControllerResponse<Void> res = jodController.updateVoteCount(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                case Protocol.DELETE_JOD: {
                    //DELETE_JOD|requesterRole|id
                    if (parts.length < 3)
                        return Protocol.error("Missing fields for DELETE_JOD.");
                    ControllerResponse<Void> res = jodController.deleteJokeOfTheDayById(
                            parts[1], Integer.parseInt(parts[2]));
                    return serialize(res, null);
                }

                default:
                    return Protocol.error("Unknown action: " + action);
            }

        } catch (NumberFormatException e) {
            return Protocol.error("Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Protocol.error("Server error: " + e.getMessage());
        }
    }

    //SERIALIZERS
    //Convert model objects into pipe-separated strings so they can be sent over the network as plain text
    //Format: field1,field2,field3

    private String serializeUser(User u) {
        if (u == null) return "";
        return u.getUserId()      + ","
                + u.getUsername()    + ","
                + u.getEmail()       + ","
                + u.getRole();
    }

    private String serializeUserList(List<User> users) {
        if (users == null || users.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (User u : users) {
            sb.append(serializeUser(u)).append(Protocol.LIST_SEPARATOR);
        }
        return sb.toString();
    }

    private String serializeJoke(Joke j) {
        if (j == null) return "";
        return j.getJokeId()    + ","
                + j.getCreatorId() + ","
                + j.getStatus();
    }

    private String serializeJokeList(List<Joke> jokes) {
        if (jokes == null || jokes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Joke j : jokes) {
            sb.append(serializeJoke(j)).append(Protocol.LIST_SEPARATOR);
        }
        return sb.toString();
    }

    private String serializeVote(Vote v) {
        if (v == null) return "";
        return v.getVoteId() + ","
                + v.getUserId() + ","
                + v.getJokeId();
    }

    private String serializeVoteList(List<Vote> votes) {
        if (votes == null || votes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Vote v : votes) {
            sb.append(serializeVote(v)).append(Protocol.LIST_SEPARATOR);
        }
        return sb.toString();
    }

    private String serializeJod(JokeOfTheDay j) {
        if (j == null) return "";
        return j.getJodId()     + ","
                + j.getJokeId()    + ","
                + j.getTotalVotes();
    }

    //Builds the final response string from a ControllerResponse and the serialized data:
    private String serialize(ControllerResponse<?> res, String serializedData) {
        if (res.isSuccess()) {
            if (serializedData == null || serializedData.isBlank()) {
                return Protocol.success(res.getMessage());
            }
            return Protocol.success(res.getMessage()
                    + Protocol.SEPARATOR_PLAIN + serializedData);
        }
        return Protocol.error(res.getMessage());
    }
}

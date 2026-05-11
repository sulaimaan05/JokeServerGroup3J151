package Protocol;

public class Protocol {

    //Separators:
    public static final String SEPARATOR = "\\|";
    public static final String SEPARATOR_PLAIN = "|";
    public static final String LIST_SEPARATOR = "~";
    public static final String ITEM_SEPARATOR = ":";

    //Response prefixes:
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    //Auth actions:
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";

    //User actions:
    public static final String GET_USER_BY_ID = "GET_USER_BY_ID";
    public static final String GET_ALL_USERS = "GET_ALL_USERS";
    public static final String UPDATE_EMAIL = "UPDATE_EMAIL";
    public static final String UPDATE_PASSWORD = "UPDATE_PASSWORD";
    public static final String UPDATE_USERNAME = "UPDATE_USERNAME";
    public static final String UPGRADE_ROLE = "UPGRADE_ROLE";
    public static final String DOWNGRADE_ROLE = "DOWNGRADE_ROLE";
    public static final String DELETE_ACCOUNT = "DELETE_ACCOUNT";

    //Joke actions:
    public static final String SUBMIT_JOKE = "SUBMIT_JOKE";
    public static final String GET_APPROVED = "GET_APPROVED";
    public static final String GET_MY_JOKES = "GET_MY_JOKES";
    public static final String GET_ALL_JOKES = "GET_ALL_JOKES";
    public static final String GET_JOKE_BY_ID = "GET_JOKE_BY_ID";
    public static final String EDIT_JOKE = "EDIT_JOKE";
    public static final String DELETE_JOKE = "DELETE_JOKE";

    //Moderation actions:
    public static final String GET_PENDING = "GET_PENDING";
    public static final String APPROVE_JOKE = "APPROVE_JOKE";
    public static final String REJECT_JOKE = "REJECT_JOKE";
    public static final String GET_MOD_REQUESTS = "GET_MOD_REQUESTS";
    public static final String APPROVE_MOD_REQUEST = "APPROVE_MOD_REQUEST";
    public static final String DENY_MOD_REQUEST = "DENY_MOD_REQUEST";

    //Vote actions:
    public static final String UPVOTE = "UPVOTE";
    public static final String DOWNVOTE = "DOWNVOTE";
    public static final String RETRACT_VOTE = "RETRACT_VOTE";
    public static final String GET_VOTE_COUNT = "GET_VOTE_COUNT";
    public static final String GET_VOTES_JOKE = "GET_VOTES_JOKE";
    public static final String GET_VOTES_USER = "GET_VOTES_USER";
    public static final String DELETE_VOTE = "DELETE_VOTE";

    //Joke of the day actions:
    public static final String GET_JOD = "GET_JOD";
    public static final String GET_JOD_BY_ID = "GET_JOD_BY_ID";
    public static final String REFRESH_JOD = "REFRESH_JOD";
    public static final String UPDATE_JOD_VOTES = "UPDATE_JOD_VOTES";
    public static final String DELETE_JOD = "DELETE_JOD";

    //Request builder:
    //Used by the Client/View to build request strings to send to the Server:

    //Auth:
    public static String buildRegister(String username, String password, String email, String role) {
        return REGISTER + SEPARATOR_PLAIN + username + SEPARATOR_PLAIN + password + SEPARATOR_PLAIN + email + SEPARATOR_PLAIN + role;
    }

    public static String buildLogin(String username, String password) {
        return LOGIN + SEPARATOR_PLAIN + username + SEPARATOR_PLAIN + password;
    }

    //User:
    public static String buildGetUserById(int userId) {
        return GET_USER_BY_ID + SEPARATOR_PLAIN + userId;
    }

    public static String buildGetAllUsers(String requesterRole) {
        return GET_ALL_USERS + SEPARATOR_PLAIN + requesterRole;
    }

    public static String buildUpdateEmail(int userId, String newEmail) {
        return UPDATE_EMAIL + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + newEmail;
    }

    public static String buildUpdatePassword(int userId, String newPassword) {
        return UPDATE_PASSWORD + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + newPassword;
    }

    public static String buildUpdateUsername(int userId, String newUsername) {
        return UPDATE_USERNAME + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + newUsername;
    }

    public static String buildUpgradeRole(int userId, String newRole) {
        return UPGRADE_ROLE + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + newRole;
    }

    public static String buildDowngradeRole(int userId, String newRole) {
        return DOWNGRADE_ROLE + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + newRole;
    }

    public static String buildDeleteAccount(String username) {
        return DELETE_ACCOUNT + SEPARATOR_PLAIN + username;
    }

    //Jokes:
    public static String buildSubmitJoke(int creatorId, String creatorRole, String jokeText) {
        return SUBMIT_JOKE + SEPARATOR_PLAIN + creatorId + SEPARATOR_PLAIN + creatorRole + SEPARATOR_PLAIN + jokeText;
    }

    public static String buildGetApprovedJokes() {
        return GET_APPROVED;
    }

    public static String buildGetMyJokes(int creatorId, String creatorRole) {
        return GET_MY_JOKES + SEPARATOR_PLAIN + creatorId + SEPARATOR_PLAIN + creatorRole;
    }

    public static String buildGetAllJokes(String requesterRole) {
        return GET_ALL_JOKES + SEPARATOR_PLAIN + requesterRole;
    }

    public static String buildGetJokeById(int jokeId, String requesterRole) {
        return GET_JOKE_BY_ID + SEPARATOR_PLAIN + jokeId + SEPARATOR_PLAIN + requesterRole;
    }

    public static String buildEditJoke(int requesterId, int jokeId, String newSetup, String newPunchline, String newCategory) {
        return EDIT_JOKE + SEPARATOR_PLAIN + requesterId + SEPARATOR_PLAIN + jokeId + SEPARATOR_PLAIN + newSetup + SEPARATOR_PLAIN + newPunchline    + SEPARATOR_PLAIN + newCategory;
    }

    public static String buildDeleteJoke(int requesterId, String requesterRole, int jokeId) {
        return DELETE_JOKE + SEPARATOR_PLAIN + requesterId + SEPARATOR_PLAIN + requesterRole + SEPARATOR_PLAIN + jokeId;
    }

    //Moderation:
    public static String buildGetPending(String moderatorRole) {
        return GET_PENDING + SEPARATOR_PLAIN + moderatorRole;
    }

    public static String buildApproveJoke(String moderatorRole, int jokeId) {
        return APPROVE_JOKE + SEPARATOR_PLAIN + moderatorRole + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildRejectJoke(String moderatorRole, int jokeId) {
        return REJECT_JOKE + SEPARATOR_PLAIN + moderatorRole + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildGetModRequests(String moderatorRole) {
        return GET_MOD_REQUESTS + SEPARATOR_PLAIN + moderatorRole;
    }

    public static String buildApproveModRequest(String moderatorRole, int targetUserId) {
        return APPROVE_MOD_REQUEST + SEPARATOR_PLAIN + moderatorRole + SEPARATOR_PLAIN + targetUserId;
    }

    public static String buildDenyModRequest(String moderatorRole, int targetUserId) {
        return DENY_MOD_REQUEST + SEPARATOR_PLAIN + moderatorRole + SEPARATOR_PLAIN + targetUserId;
    }

    //Votes:
    public static String buildUpvote(int userId, int jokeId) {
        return UPVOTE + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildDownvote(int userId, int jokeId) {
        return DOWNVOTE + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildRetractVote(int userId, int jokeId) {
        return RETRACT_VOTE + SEPARATOR_PLAIN + userId + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildGetVoteCount(int jokeId) {
        return GET_VOTE_COUNT + SEPARATOR_PLAIN + jokeId;
    }

    public static String buildGetVotesForJoke(int jokeId, String requesterRole) {
        return GET_VOTES_JOKE + SEPARATOR_PLAIN + jokeId + SEPARATOR_PLAIN + requesterRole;
    }

    public static String buildGetVotesByUser(int requesterId, String requesterRole, int targetUserId) {
        return GET_VOTES_USER + SEPARATOR_PLAIN + requesterId + SEPARATOR_PLAIN + requesterRole   + SEPARATOR_PLAIN + targetUserId;
    }

    public static String buildDeleteVote(String requesterRole, int voteId) {
        return DELETE_VOTE + SEPARATOR_PLAIN + requesterRole + SEPARATOR_PLAIN + voteId;
    }

    //Joke of the Day:
    public static String buildGetJod() {
        return GET_JOD;
    }

    public static String buildGetJodById(int id) {
        return GET_JOD_BY_ID + SEPARATOR_PLAIN + id;
    }

    public static String buildRefreshJod(String requesterRole) {
        return REFRESH_JOD + SEPARATOR_PLAIN + requesterRole;
    }

    public static String buildUpdateJodVotes(int jodId, int totalVotes) {
        return UPDATE_JOD_VOTES + SEPARATOR_PLAIN + jodId + SEPARATOR_PLAIN + totalVotes;
    }

    public static String buildDeleteJod(String requesterRole, int id) {
        return DELETE_JOD + SEPARATOR_PLAIN + requesterRole + SEPARATOR_PLAIN + id;
    }

    //Response builders:
    //Used by the Server to build response strings to send back.
    public static String success(String data) {
        return SUCCESS + SEPARATOR_PLAIN + data;
    }

    public static String error(String reason) {
        return ERROR + SEPARATOR_PLAIN + reason;
    }

    //Response parsers:
    //Used by the Client/View to read responses from the Server:
    public static boolean isSuccess(String response) {
        return response != null && response.startsWith(SUCCESS);
    }

    public static boolean isError(String response) {
        return response != null && response.startsWith(ERROR);
    }

    //Extracts everything after SUCCESS| or ERROR|:
    public static String getData(String response) {
        String[] parts = response.split(SEPARATOR, 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
package Client;

import Protocol.Protocol;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String SERVER_IP   = "192.168.8.128";
    private static final int    SERVER_PORT = 5000;

    private Socket         socket;
    private BufferedReader in;
    private PrintWriter    out;

    //Connect to the server.
    //Returns true if successful, false if connection failed:
    public boolean connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in     = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out    = new PrintWriter(
                    socket.getOutputStream(), true);
            System.out.println("[Client] Connected to server at "
                    + SERVER_IP + ":" + SERVER_PORT);
            return true;
        } catch (IOException e) {
            System.err.println("[Client] Could not connect: " + e.getMessage());
            return false;
        }
    }

    //Send a request string and return the server's response.
    //This is the only method your View screens need to call:
    public String sendRequest(String request) {
        try {
            System.out.println("[Client] Sending:  " + request);
            out.println(request);
            String response = in.readLine();
            System.out.println("[Client] Received: " + response);
            return response;
        } catch (IOException e) {
            System.err.println("[Client] Communication error: " + e.getMessage());
            return Protocol.error("Connection lost.");
        }
    }

    //Disconnect cleanly when the user logs out or closes the app:
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[Client] Disconnected from server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}

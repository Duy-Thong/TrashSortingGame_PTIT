package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("Server is running on port " + PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("Waiting for packet...");
                socket.receive(packet);
                System.out.println("Packet received!");
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                handleClientRequest(message, packet, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(String message, DatagramPacket packet, DatagramSocket socket) {
        if (message == null || message.isEmpty()) {
            System.out.println("Received an empty or null message.");
            return;
        }

        String[] parts = message.split("&");
        String type = parts[0].split("=")[1];

        String response;
        if (type.equals("login")) {
            String username = parts[1].split("=")[1];
            String password = parts[2].split("=")[1];
            boolean isAuthenticated = authenticate(username, password);
            response = isAuthenticated ? "login success" : "login failure";
        } else if (type.equals("register")) {
            String username = parts[1].split("=")[1];
            String password = parts[2].split("=")[1];
            boolean isRegistered = register(username, password);
            response = isRegistered ? "registration_success" : "registration_failure";
        } else {
            response = "unknown_request";
        }

        System.out.println("Sending response: " + response);

        byte[] responseBuffer = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean authenticate(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/trashsortinggame";
        String dbUser = "root"; // Change to your database username
        String dbPassword = ""; // Change to your database password

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); // It's advisable to use a hashed password

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean register(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/trashsortinggame";
        String dbUser = "root"; // Change to your database username
        String dbPassword = ""; // Change to your database password

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            // Check if the username already exists
            String checkQuery = "SELECT * FROM account WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false; // Account already exists
                }
            }

            // Generate UUID for the account
            String accountUUID = UUID.randomUUID().toString();

            // Insert the new account
            String insertAccountQuery = "INSERT INTO account (accountID, username, password, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            try (PreparedStatement insertAccountStmt = conn.prepareStatement(insertAccountQuery)) {
                insertAccountStmt.setString(1, accountUUID); // Insert as string
                insertAccountStmt.setString(2, username);
                insertAccountStmt.setString(3, password); // Consider using a hashed password
                insertAccountStmt.executeUpdate();
            }

            // Now create the player record using the accountID
            return createPlayer(accountUUID);

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }

    private static boolean createPlayer(String accountID) {
        String url = "jdbc:mysql://localhost:3306/trashsortinggame";
        String dbUser = "root"; // Change to your database username
        String dbPassword = ""; // Change to your database password

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            // Generate a new playerID
            String playerID = UUID.randomUUID().toString();

            // Insert the new player record
            String insertPlayerQuery = "INSERT INTO player (playerID, accountID, total_games, total_wins, total_score, average_score, created_at, updated_at) VALUES (?, ?, 0, 0, 0, 0, NOW(), NOW())";
            try (PreparedStatement insertPlayerStmt = conn.prepareStatement(insertPlayerQuery)) {
                insertPlayerStmt.setString(1, playerID);
                insertPlayerStmt.setString(2, accountID);
                insertPlayerStmt.executeUpdate();
                return true; // Player created successfully
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
}

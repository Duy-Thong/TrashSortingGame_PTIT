package Server;

import Client.controller.ProfileController;
import Server.model.Player;
import Server.model.PlayerGame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Server {
    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trashsortinggame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("Server is running on port " + PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                handleClientRequest(message, packet, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(String message, DatagramPacket packet, DatagramSocket socket) {
        try {
            if (message == null || message.isEmpty()) {
                sendResponse("error: invalid request", packet, socket);
                return;
            }

            String[] parts = message.split("&");
            String type = parts[0].split("=")[1];
            String response;

            switch (type) {
                case "login":
                    String username = parts[1].split("=")[1];
                    String password = parts[2].split("=")[1];
                    response = authenticate(username, password) ? "login success" : "login failure";
                    break;
                case "register":
                    username = parts[1].split("=")[1];
                    password = parts[2].split("=")[1];
                    response = register(username, password) ? "registration_success" : "registration failure";
                    break;
                case "getAccountID":
                    username = parts[1].split("=")[1];
                    response = getAccountID(username);
                    break;
                case "getPlayerID":
                    String accountID = parts[1].split("=")[1];
                    response = getPlayerID(accountID);
                    break;
                case "profile":
                    String playerID = parts[1].split("=")[1];
                    Player player = getPlayerProfile(playerID);
                    if (player != null) {
                        response = String.format("playerId=%s&username=%s&totalGames=%d&totalWins=%d&totalScore=%d&averageScore=%d&createdAt=%s&updatedAt=%s",
                                player.getPlayerID(), player.getUsername(), player.getTotalGames(),
                                player.getTotalWins(), player.getTotalScore(), player.getAverageScore(),
                                player.getCreatedAt().toString(), player.getUpdatedAt().toString());
                    } else {
                        response = "error: player not found";
                    }
                    break;
                case "history":
                    // Lấy playerID từ yêu cầu
                    playerID = parts[1].split("=")[1];

                    // Lấy lịch sử người chơi
                    List<PlayerGame> historyList = getPlayerHistory(playerID);

                    // Đóng gói lịch sử vào chuỗi để gửi về client
                    StringBuilder responseBuilder = new StringBuilder();

                    // Kiểm tra nếu lịch sử không rỗng
                    if (historyList.isEmpty()) {
                        responseBuilder.append("error: no history found for player");
                    } else {
                        for (PlayerGame game : historyList) {
                            responseBuilder.append(String.format("gameID=%s&joinTime=%s&leaveTime=%s&playDuration=%d&score=%d&result=%s&isFinal=%b|",
                                    game.getGameID(), game.getJoinTime(), game.getLeaveTime(), game.getPlayDuration(),
                                    game.getScore(), game.getResult(), game.isFinal()));
                        }
                    }

                    // Gửi phản hồi về cho client
                    response = responseBuilder.toString();
                    System.out.println("Response to client: " + response);
                    break;
                    case "getAccount":
                    accountID = parts[1].split("=")[1];
                    response = getAccountbyID(accountID);
                    break;
                default:
                    response = "error: unknown request";
            }

            sendResponse(response, packet, socket);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse("error: server error", packet, socket);
        }
    }

    private static void sendResponse(String response, DatagramPacket packet, DatagramSocket socket) {
        try {
            byte[] responseBuffer = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Authenticate user with username and password
    private static boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM account WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Register a new user with username and password
    private static boolean register(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if username already exists
            String checkQuery = "SELECT * FROM account WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) return false;
            }

            // Insert new account
            String accountUUID = UUID.randomUUID().toString();
            String insertAccountQuery = "INSERT INTO account (accountID, username, password, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            try (PreparedStatement insertAccountStmt = conn.prepareStatement(insertAccountQuery)) {
                insertAccountStmt.setString(1, accountUUID);
                insertAccountStmt.setString(2, username);
                insertAccountStmt.setString(3, password);
                insertAccountStmt.executeUpdate();
            }

            return createPlayer(accountUUID);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a new player linked to an account
    private static boolean createPlayer(String accountID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String playerID = UUID.randomUUID().toString();
            String insertPlayerQuery = "INSERT INTO player (playerID, accountID, total_games, total_wins, total_score, average_score, created_at, updated_at) VALUES (?, ?, 0, 0, 0, 0, NOW(), NOW())";
            try (PreparedStatement insertPlayerStmt = conn.prepareStatement(insertPlayerQuery)) {
                insertPlayerStmt.setString(1, playerID);
                insertPlayerStmt.setString(2, accountID);
                insertPlayerStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve account ID by username
    private static String getAccountID(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT accountID FROM account WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("accountID");
                }
                return "error: user not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: server error";
        }
    }
    private static String getAccountbyID(String accountID) {
        if (accountID == null || accountID.isEmpty()) {
            System.err.println("Error: accountID is null or empty");
            return null; // or throw an IllegalArgumentException
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM account WHERE accountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                     // Assuming you have an accountID column
                    String username = rs.getString("username"); // Assuming you have a username column
                    String password = rs.getString("password"); // Assuming you have a password column
                    String role = rs.getString("role"); // Assuming you have a role column
                    return String.format("username=%s&password=%s&role=%s",
                            username, password, role);
                }
                System.err.println("Error: User not found");
            }
        } catch (SQLException e) {
            System.err.println("Error: Database error - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: Unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    // Retrieve player ID by account ID
    private static String getPlayerID(String accountID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT playerID FROM player WHERE accountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("playerID");
                }
                return "error: player not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: server error";
        }
    }

    private static Player getPlayerProfile(String playerID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.total_games, p.total_wins, p.total_score, p.average_score, a.username, p.created_at, p.updated_at "
                    + "FROM player p "
                    + "JOIN account a ON p.accountID = a.accountID "
                    + "WHERE p.playerID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    int totalGames = rs.getInt("total_games");
                    int totalWins = rs.getInt("total_wins");
                    int totalScore = rs.getInt("total_score");
                    int avgScore = rs.getInt("average_score");
                    Timestamp createdAt = rs.getTimestamp("created_at");  // Lấy timestamp cho created_at
                    Timestamp updatedAt = rs.getTimestamp("updated_at");  // Lấy timestamp cho updated_at

                    // Trả về đối tượng Player với các thông tin đã lấy
                    return new Player(playerID, username, totalGames, totalWins, totalScore, avgScore, createdAt, updatedAt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Hoặc trả về một đối tượng lỗi nếu cần
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    public static List<PlayerGame> getPlayerHistory(String playerID) {
        List<PlayerGame> historyList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT gameID, join_time, leave_time, play_duration, score, result, is_final " +
                    "FROM player_game " +
                    "WHERE playerID = ? " +
                    "ORDER BY join_time DESC"; // Sắp xếp theo thời gian tham gia

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String gameID = rs.getString("gameID");
                    Timestamp joinTime = rs.getTimestamp("join_time");
                    Timestamp leaveTime = rs.getTimestamp("leave_time");
                    Integer playDuration = rs.getInt("play_duration");
                    int score = rs.getInt("score");
                    String result = rs.getString("result");
                    boolean isFinal = rs.getBoolean("is_final");

                    // Thêm đối tượng PlayerGame vào danh sách
                    historyList.add(new PlayerGame(playerID, gameID, joinTime, leaveTime, playDuration, score, result, isFinal));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return historyList; // Trả về danh sách PlayerGame
    }
}
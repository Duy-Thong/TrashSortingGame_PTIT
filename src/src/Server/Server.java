package Server;

import Server.model.Account;
import Server.model.Player;
import Server.model.PlayerGame;
import Server.model.ClientInfo;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static Client.controller.admin.AccountManagementController.deleteAccount;

public class Server {
    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trashsortinggame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final List<ClientInfo> clients = new ArrayList<>();

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
                    if (response.equals("login success")) {
                        String accountID = getAccountID(username);
                        String playerID = getPlayerID(accountID);
                        clients.add(new ClientInfo(accountID, playerID, packet.getAddress(), packet.getPort()));
                        System.out.println("Client " + playerID + " connected from " + packet.getAddress() + ":" + packet.getPort());
                    }
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
                case "getAccount":
                    accountID = parts[1].split("=")[1];
                    response = getAccountbyID(accountID);
                    break;
                case "profile":
                    String playerID = parts[1].split("=")[1];
                    Player player = getPlayerProfile(playerID);
                    if (player != null) {
                        response = String.format("playerId=%s&username=%s&totalGames=%d&totalWins=%d&totalScore=%d&averageScore=%d&status=%d&isPlaying=%d&createdAt=%s",
                                player.getPlayerID(), player.getUsername(), player.getTotalGames(),
                                player.getTotalWins(), player.getTotalScore(), player.getAverageScore(),
                                player.getStatus(), player.getIsPlaying(), player.getCreatedAt().toString());
                    } else {
                        response = "error: player not found";
                    }
                    break;
                case "history":
                    playerID = parts[1].split("=")[1];

                    List<PlayerGame> historyList = getPlayerHistory(playerID);
                    StringBuilder responseBuilder = new StringBuilder();

                    if (historyList.isEmpty()) {
                        responseBuilder.append("error: no history found for player");
                    } else {
                        for (PlayerGame game : historyList) {
                            responseBuilder.append(String.format("gameID=%s&joinTime=%s&leaveTime=%s&playDuration=%d&score=%d&result=%s&isFinal=%b|",
                                    game.getGameID(), game.getJoinTime(), game.getLeaveTime(), game.getPlayDuration(),
                                    game.getScore(), game.getResult(), game.isFinal()));
                        }
                    }

                    response = responseBuilder.toString();
                    System.out.println("Response to client: " + response);
                    break;
                case "rank":
                    List<Player> playerList = getAllPlayers();

                    playerList.sort((p1, p2) -> {
                        if (p2.getTotalScore() != p1.getTotalScore()) {
                            return Integer.compare(p2.getTotalScore(), p1.getTotalScore());
                        } else if (p2.getTotalWins() != p1.getTotalWins()) {
                            return Integer.compare(p2.getTotalWins(), p1.getTotalWins());
                        } else {
                            return Integer.compare(p2.getTotalGames(), p1.getTotalGames());
                        }
                    });

                    StringBuilder rankResponse = new StringBuilder();
                    for (Player p : playerList) {
                        rankResponse.append(String.format("playerId=%s&username=%s&totalGames=%d&totalWins=%d&totalScore=%d&averageScore=%d&status=%d&isPlaying=%d&createdAt=%s|",
                                p.getPlayerID(), p.getUsername(), p.getTotalGames(), p.getTotalWins(), p.getTotalScore(), p.getAverageScore(), p.getStatus(), p.getIsPlaying(), p.getCreatedAt()));
                    }

                    response = rankResponse.toString();
                    break;
                case "friends":
                    playerID = parts[1].split("=")[1];
                    List<Player> friendsList = getListFriends(playerID);
                    StringBuilder responseFriends = new StringBuilder();

                    if (friendsList.isEmpty()) {
                        responseFriends.append("error: no history found for player");
                    } else {
                        for (Player p : friendsList) {
                            responseFriends.append(String.format("playerId=%s&username=%s&totalGames=%d&totalWins=%d&totalScore=%d&averageScore=%d&status=%d&isPlaying=%d&createdAt=%s|",
                                    p.getPlayerID(), p.getUsername(), p.getTotalGames(), p.getTotalWins(), p.getTotalScore(), p.getAverageScore(), p.getStatus(), p.getIsPlaying(), p.getCreatedAt()));
                        }
                    }

                    response = responseFriends.toString();
                    System.out.println("Response to client: " + response);
                    break;
                    case "logout":
                    playerID = parts[1].split("=")[1];
                    if (logout(playerID)) {
                        response = "logout success";
                        System.out.println("Client " + playerID + " disconnected from " + packet.getAddress() + ":" + packet.getPort());
                    } else {
                        response = "logout failure";
                        System.out.println("Client " + playerID + " failed to disconnect from " + packet.getAddress() + ":" + packet.getPort());
                    }
                    break;
                    case "getAllAccount":
                    List<Account> accountList = getAllAccount();
                    StringBuilder responseAccount = new StringBuilder();
                    for (Account a : accountList) {
                        responseAccount.append(String.format("accountID=%s&username=%s&password=%s&role=%s|",
                                a.getAccountID(), a.getUsername(), a.getPassword(), a.getRole()));
                    }
                    response = responseAccount.toString();
                    break;
                    case "deleteAccount":
                    accountID = parts[1].split("=")[1];
                    if (deleteAccount(accountID)) {
                        response = "delete success";
                    } else {
                        response = "delete failure";
                    }
                    break;
//                case "invite":
//                    String currentPlayerID = parts[1].split("=")[1];
//                    String invitedPlayerID = parts[2].split("=")[1];
//
//                    InetAddress invitedPlayerAddress = InetAddress.getByName("IP_INVITED_PLAYER");
//                    DatagramPacket invitePacket = new DatagramPacket(message.getBytes(), message.length(),
//                            invitedPlayerAddress, INVITED_PLAYER_PORT);
//                    socket.send(invitePacket);
//
//                    // Gửi phản hồi về currentPlayer
//                    String responseMessage = "Lời mời đã được gửi đến " + invitedPlayerID + "!";
//                    DatagramPacket responsePacket = new DatagramPacket(responseMessage.getBytes(),
//                            responseMessage.length(), packet.getAddress(), packet.getPort());
//                    socket.send(responsePacket);
//                    break;
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
    public static boolean logout(String playerID) {
        ClientInfo client = null;
        for (ClientInfo c : clients) {
            if (c.getPlayerID().equals(playerID)) {
                client = c;
                break;
            }
        }
        if (client != null) {
            clients.remove(client);
            return true;
        }
        return false;
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
            String insertAccountQuery = "INSERT INTO account (accountID, username, password,role, created_at, updated_at) VALUES (?, ?, ?, ?,NOW(), NOW())";
            try (PreparedStatement insertAccountStmt = conn.prepareStatement(insertAccountQuery)) {
                insertAccountStmt.setString(1, accountUUID);
                insertAccountStmt.setString(2, username);
                insertAccountStmt.setString(3, password);
                insertAccountStmt.setString(4, "player");
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
            String query = "SELECT p.total_games, p.total_wins, p.total_score, p.average_score, a.username, p.status, p.isPlaying, p.created_at "
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
                    int status = rs.getInt("status");
                    int isPlaying = rs.getInt("isPlaying");
                    Timestamp createdAt = rs.getTimestamp("created_at");  // Lấy timestamp cho created_at

                    // Trả về đối tượng Player với các thông tin đã lấy
                    return new Player(playerID, username, totalGames, totalWins, totalScore, avgScore, status, isPlaying, createdAt);
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

    // Hàm lấy danh sách tất cả các player từ cơ sở dữ liệu
    private static List<Player> getAllPlayers() {
        List<Player> playerList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT p.playerID, p.total_games, p.total_wins, p.total_score, a.username, p.average_score, p.status, p.isPlaying, p.created_at"
                    + " FROM player p"
                    + " JOIN account a ON p.accountID = a.accountID";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String playerID = rs.getString("playerID");
                    String username = rs.getString("username");
                    int totalGames = rs.getInt("total_games");
                    int totalWins = rs.getInt("total_wins");
                    int totalScore = rs.getInt("total_score");
                    int avgScore = rs.getInt("average_score");
                    int status = rs.getInt("status");
                    int isPlaying = rs.getInt("isPlaying");
                    Timestamp createdAt = rs.getTimestamp("created_at");  // Lấy timestamp cho created_at

                    playerList.add(new Player(playerID, username, totalGames, totalWins, totalScore, avgScore, status, isPlaying, createdAt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playerList;
    }

    // Hàm lấy danh sách tất cả các friends từ cơ sở dữ liệu
    private static List<Player> getListFriends(String playerId) {
        List<Player> playerList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Thay đổi câu truy vấn để lấy bạn bè online trừ bản thân
            String query = "SELECT p.playerID, p.total_games, p.total_wins, p.total_score, a.username, p.average_score, p.status, p.isPlaying, p.created_at "
                    + "FROM player p "
                    + "JOIN account a ON p.accountID = a.accountID "
                    + "WHERE p.status = 1 AND p.isPlaying = 0 AND p.playerID != ?"; // Lọc ra bạn bè online và không phải là bản thân

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerId); // Thiết lập giá trị playerID vào câu truy vấn
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String friendID = rs.getString("playerID");
                    String username = rs.getString("username");
                    int totalGames = rs.getInt("total_games");
                    int totalWins = rs.getInt("total_wins");
                    int totalScore = rs.getInt("total_score");
                    int avgScore = rs.getInt("average_score");
                    int status = rs.getInt("status");
                    int isPlaying = rs.getInt("isPlaying");
                    Timestamp createdAt = rs.getTimestamp("created_at");  // Lấy timestamp cho created_at

                    playerList.add(new Player(friendID, username, totalGames, totalWins, totalScore, avgScore, status, isPlaying, createdAt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playerList;
    }
    private static List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM account";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String accountID = rs.getString("accountID");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    accountList.add(new Account(accountID,username, password, role));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }
    private static boolean deleteAccount(String accountID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM account WHERE accountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
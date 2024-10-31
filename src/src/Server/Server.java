package Server;

import Server.model.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {
    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/trashsortinggame";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final List<ClientInfo> clients = new ArrayList<>();
    private static final List<Room> rooms = new ArrayList<>();
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

            if (type.equals("invite")) {
                String currentPlayerID = parts[1].split("=")[1];
                String invitedPlayerID = parts[2].split("=")[1];
                String invitedPlayerName = parts[3].split("=")[1];

                ClientInfo invitedClient = findClientByPlayerID(invitedPlayerID);

                if (invitedClient != null) {
                    InetAddress invitedPlayerAddress = invitedClient.getAddress();
                    int invitedPlayerPort = 12346;

                    String inviteMessage = "type=invited&currentPlayerID=" + currentPlayerID + "&invitedPlayerID=" + invitedPlayerID + "&invitedPlayerName=" + invitedPlayerName;
                    DatagramPacket invitePacket = new DatagramPacket(inviteMessage.getBytes(), inviteMessage.length(),
                            invitedPlayerAddress, invitedPlayerPort);
                    socket.send(invitePacket);
                    System.out.println("Response to client: " + inviteMessage);
                } else {
                    System.out.println("Không tìm thấy người chơi với ID: " + invitedPlayerID);
                }
            }

            else if (type.equals("response")) {
                String currentPlayerID = parts[1].split("=")[1];
                String invitedPlayerID = parts[2].split("=")[1];
                String status = parts[3].split("=")[1];

                ClientInfo currentClient = findClientByPlayerID(currentPlayerID);
                ClientInfo invitedClient = findClientByPlayerID(invitedPlayerID);

                InetAddress currentAddress = currentClient.getAddress();
                int currentPort = 12344;
                InetAddress invitedAddress = invitedClient.getAddress();

                if (status.equals("accepted")) {
                    System.out.println("Player " + invitedPlayerID + " accepted the invite. Creating a new game...");

                    // Tạo game mới
                    String gameID = createNewGame();

                    // Tạo hai bản ghi PlayerGame cho hai người chơi
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    PlayerGame player1Game = new PlayerGame(currentPlayerID, gameID, currentTime, null, 0, 0, "", false);
                    PlayerGame player2Game = new PlayerGame(invitedPlayerID, gameID, currentTime, null, 0, 0, "", false);
                    savePlayerGame(player1Game);
                    savePlayerGame(player2Game);

                    // Cập nhật trạng thái isPlaying của người chơi
                    updatePlayerIsPlaying(currentPlayerID, true);
                    updatePlayerIsPlaying(invitedPlayerID, true);

                    // Gửi gói tin thông báo tạo game cho hai người chơi
                    String notificationMessage = "type=accepted&gameID=" + gameID + "&player1=" + currentPlayerID + "&player2=" + invitedPlayerID;
                    byte[] sendData = notificationMessage.getBytes();
                    rooms.add(new Room(gameID,currentPlayerID,invitedPlayerID));

                    DatagramPacket player1Packet = new DatagramPacket(sendData, sendData.length, currentAddress, currentPort);
                    socket.send(player1Packet);

                    DatagramPacket player2Packet = new DatagramPacket(sendData, sendData.length, invitedAddress, 12346);
                    socket.send(player2Packet);

                    System.out.println("Game started and notification sent to both players.");
                } else {
                    String notificationMessage = "type=declined&player1=" + currentPlayerID;
                    byte[] sendData = notificationMessage.getBytes();

                    DatagramPacket player1Packet = new DatagramPacket(sendData, sendData.length, currentAddress, currentPort);
                    socket.send(player1Packet);

                    System.out.println("Player " + invitedPlayerID + " declined the invite.");
                    System.out.println("Player1: " + currentAddress + ":" + currentPort);
                }
            }
            else if (type.equals("UPDATE_SCORE")) {
                // id player gửi điểm đi
                String playerId = parts[1].split("=")[1];
                String newScore = parts[2].split("=")[1];
                String roomId = parts[3].split("=")[1];
                response = "type=UPDATE_SCORE&newScore=" + newScore;
                for (Room room : rooms) {
                    if(room.getRoomId().equals(roomId))
                    {
                        String idPlayerTaget = "";
                        if(playerId.equals(room.getPlayerId1())) {
                            idPlayerTaget = room.getPlayerId2();
                        } else {
                            idPlayerTaget = room.getPlayerId1();
                        }
                        ClientInfo clientInfo = findClientByPlayerID(idPlayerTaget);
                        if (clientInfo != null) {
                            DatagramPacket updateScorePacket = new DatagramPacket(response.getBytes(), response.length(),
                                    clientInfo.getAddress(), 12349);
                            socket.send(updateScorePacket);
                            System.out.println("Response to client: "+ clientInfo.getAddress() + "response:" +response);
                        } else {
                            System.out.println("Không tìm thấy người chơi với ID: " + room.getPlayerId2());
                        }
                        break;
                    }
                }

            }
            else if (type.equals("update_player_game")) {
                // id player gửi điểm đi
                String playerId = parts[1].split("=")[1];
                String gameId = parts[2].split("=")[1];
                String score = parts[3].split("=")[1];
                String result = parts[4].split("=")[1];
                updatePlayer_game(playerId, gameId, Integer.parseInt(score), result);
                response = "type=end_socket";
                for (Room room : rooms) {
                    if(room.getRoomId().equals(gameId))
                    {
                        String idPlayerTaget = room.getPlayerId1();;
                        ClientInfo clientInfo = findClientByPlayerID(idPlayerTaget);
                        if (clientInfo != null) {
                            DatagramPacket updateScorePacket = new DatagramPacket(response.getBytes(), response.length(),
                                    clientInfo.getAddress(), 12349);
                            socket.send(updateScorePacket);
                            System.out.println("Response to client: "+ clientInfo.getAddress() + "response:" +response);
                        } else {
                            System.out.println("Không tìm thấy người chơi với ID: " + room.getPlayerId2());
                        }
                        break;
                    }
                }


            }
            else if (type.equals("update_player")) {
                // id player gửi điểm đi
                String playerId = parts[1].split("=")[1];
                String score = parts[2].split("=")[1];
                String result = parts[3].split("=")[1];
                updatePlayerStats(playerId, Integer.parseInt(score), result);
            }
            else {
                switch (type) {
                    case "login":
                        String username = parts[1].split("=")[1];
                        String password = parts[2].split("=")[1];
                        response = authenticate(username, password) ? "login success" : "login failure";
                        if (response.equals("login success")) {
                            String accountID = getAccountID(username);
                            String playerID = getPlayerID(accountID);
                            clients.add(new ClientInfo(accountID, playerID, packet.getAddress(), packet.getPort()));
                            makePlayerOnline(playerID);
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
                            makePlayerOffline(playerID);
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
                    case "updateAccount":
                        accountID = parts[1].split("=")[1];
                        username = parts[2].split("=")[1];
                        password = parts[3].split("=")[1];
                        String role = parts[4].split("=")[1];
                        if (updateAccount(accountID, username, password, role)) {
                            response = "update success";
                        } else {
                            response = "update failure";
                        }
                        break;
                    case "addAccount":
                        username = parts[1].split("=")[1];
                        password = parts[2].split("=")[1];
                        role = parts[3].split("=")[1];
                        if (addAccount(username, password, role)) {
                            response = "add success";
                        } else {
                            response = "add failure";
                        }
                        break;
                    default:
                        response = "error: unknown request";
                }

                sendResponse(response, packet, socket);
            }
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
            String insertAccountQuery = "INSERT INTO account (accountID, username, password,role, created_at, updated_at) VALUES (?, ?, ?,?, NOW(), NOW())";
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

    // Phương thức để tìm ClientInfo dựa trên playerID
    private static ClientInfo findClientByPlayerID(String playerID) {
        for (ClientInfo client : clients) {
            if (client.getPlayerID().equals(playerID)) {
                System.out.println("Đã tìm thấy client: " + client.getPlayerID() + "&" + client.getAddress() + "&" + client.getPort());
                return client;
            }
        }
        return null;
    }

    private static String createNewGame() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String gameID = UUID.randomUUID().toString();
            String insertGameQuery = "INSERT INTO game (gameID, status, start_time, end_time, total_score) VALUES (?, ?, ?, ?, 0)";
            try (PreparedStatement insertGameStmt = conn.prepareStatement(insertGameQuery)) {
                insertGameStmt.setString(1, gameID);
                insertGameStmt.setString(2, "pending");
                insertGameStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                insertGameStmt.setTimestamp(4, null);
                insertGameStmt.executeUpdate();
                System.out.println("Game created with ID: " + gameID);
                return gameID;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void savePlayerGame(PlayerGame playerGame) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertPlayerGameQuery = "INSERT INTO player_game (playerID, gameID, join_time, leave_time, play_duration, score, result, is_final) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertPlayerGameStmt = conn.prepareStatement(insertPlayerGameQuery)) {
                insertPlayerGameStmt.setString(1, playerGame.getPlayerID());
                insertPlayerGameStmt.setString(2, playerGame.getGameID());
                insertPlayerGameStmt.setTimestamp(3, playerGame.getJoinTime());
                insertPlayerGameStmt.setTimestamp(4, playerGame.getLeaveTime());
                insertPlayerGameStmt.setInt(5, playerGame.getPlayDuration());
                insertPlayerGameStmt.setInt(6, playerGame.getScore());
                insertPlayerGameStmt.setString(7, playerGame.getResult());
                insertPlayerGameStmt.setBoolean(8, playerGame.isFinal());
                insertPlayerGameStmt.executeUpdate();
                System.out.println("Saved playerGame for playerID: " + playerGame.getPlayerID() + " in gameID: " + playerGame.getGameID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updatePlayerIsPlaying(String playerID, boolean isPlaying) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updatePlayerQuery = "UPDATE player SET isPlaying = ? WHERE playerID = ?";
            try (PreparedStatement updatePlayerStmt = conn.prepareStatement(updatePlayerQuery)) {
                updatePlayerStmt.setBoolean(1, isPlaying);
                updatePlayerStmt.setString(2, playerID);
                updatePlayerStmt.executeUpdate();
                System.out.println("Updated player " + playerID + " isPlaying status to: " + isPlaying);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    private static boolean updateAccount(String accountID, String username, String password, String role) {
        String oldUsername = getAccountbyID(accountID).split("&")[0].split("=")[1];
        System.out.println("Old Username: " + oldUsername);
        System.out.println("New Username: " + username);

        if (oldUsername.equals(username)) {
            return updateAccountInDB(accountID, username, password, role);
        } else {
            if (isUsernameExists(username)) {
                return false;
            } else {
                return updateAccountInDB(accountID, username, password, role);
            }
        }
    }


    // Phương thức để thực hiện cập nhật tài khoản trong cơ sở dữ liệu
    private static boolean updateAccountInDB(String accountID, String username, String password, String role) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE account SET username = ?, password = ?, role = ?, updated_at = NOW() WHERE accountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.setString(4, accountID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean addAccount(String username, String password, String role) {
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (isUsernameExists(username)) {
            return false; // Tên đăng nhập đã tồn tại, không thêm tài khoản
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String accountID = UUID.randomUUID().toString();
            String query = "INSERT INTO account (accountID, username, password, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.setString(4, role);
                return stmt.executeUpdate() > 0; // Thêm thành công
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Thêm thất bại
        }
    }
    private static boolean isUsernameExists(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM account WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Nếu số lượng lớn hơn 0, tên đăng nhập đã tồn tại
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Mặc định là không tồn tại
    }
    private static boolean makePlayerOnline(String playerID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE player SET status = 1 WHERE playerID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static boolean makePlayerOffline(String playerID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE player SET status = 0 WHERE playerID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void sendToClient(DatagramSocket socket, String message, ClientInfo client) throws Exception {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
        socket.send(packet);
    }

    private static boolean updatePlayer_game(String playerId, String gameId, int score, String result) {
        // Kết nối tới cơ sở dữ liệu và thực hiện câu lệnh UPDATE
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Câu lệnh UPDATE cho các trường score, result, updatedAt theo playerId và gameId
            String query = "UPDATE player_game SET score = ?, result = ?, is_final = 1,play_duration = 120 , leave_time = ? WHERE playerID = ? AND gameID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Thiết lập giá trị cho các tham số
                stmt.setInt(1, score);
                stmt.setString(2, result);
                stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis())); // Cập nhật updatedAt với thời gian hiện tại
                stmt.setString(4, playerId);
                stmt.setString(5, gameId);

                // Thực thi câu lệnh và kiểm tra kết quả
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean updatePlayerStats(String playerID, int score,String result) {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Bước 1: Lấy dữ liệu hiện tại từ DB
            String selectQuery = "SELECT total_games, total_wins, total_score FROM player WHERE playerID = ?";
            selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, playerID);
            rs = selectStmt.executeQuery();
            int totalWins = 0;
            if (rs.next()) {
                // Bước 2: Tính toán giá trị mới
                int totalGames = rs.getInt("total_games") + 1;
                if(result.equalsIgnoreCase("win")){
                     totalWins = rs.getInt("total_wins") + 1;
                } else {
                     totalWins = rs.getInt("total_wins");
                }
                int totalScore = rs.getInt("total_score") + score;
                double averageScore = totalScore / (double) totalGames;

                // Bước 3: Cập nhật dữ liệu mới vào DB
                String updateQuery = "UPDATE player SET "
                        + "total_games = ?, "
                        + "total_wins = ?, "
                        + "total_score = ?, "
                        + "average_score = ?, "
                        + "isPlaying = 0, "
                        + "updated_at = ? "
                        + "WHERE playerID = ?";

                updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, totalGames);
                updateStmt.setInt(2, totalWins);
                updateStmt.setInt(3, totalScore);
                updateStmt.setInt(4, (int) averageScore);
                updateStmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis())); // updated_at với thời gian hiện tại
                updateStmt.setString(6, playerID);

                return updateStmt.executeUpdate() > 0;
            } else {
                System.out.println("Player ID không tồn tại.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Đóng kết nối và các tài nguyên
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (selectStmt != null) selectStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (updateStmt != null) updateStmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }


}
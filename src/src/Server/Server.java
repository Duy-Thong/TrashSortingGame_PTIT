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
                    Timestamp StartTime = new Timestamp(System.currentTimeMillis()+3000);
                    Timestamp leaveTime = new Timestamp(System.currentTimeMillis()+33000);
                    PlayerGame player1Game = new PlayerGame(currentPlayerID, gameID, StartTime, leaveTime, 0, 0, "", false);
                    PlayerGame player2Game = new PlayerGame(invitedPlayerID, gameID, StartTime, leaveTime, 0, 0, "", false);
                    savePlayerGame(player1Game);
                    savePlayerGame(player2Game);

                    // Cập nhật trạng thái isPlaying của người chơi
                    updatePlayerIsPlaying(currentPlayerID, true);
                    updatePlayerIsPlaying(invitedPlayerID, true);

                    // Gửi gói tin thông báo tạo game cho hai người chơi
                    String notificationMessage = "type=accepted&gameID=" + gameID + "&player1=" + currentPlayerID + "&player2=" + invitedPlayerID+"&startTime="+StartTime;
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
//                            System.out.println("Response to client: "+ clientInfo.getAddress() + "response:" +response);
                        } else {
                            System.out.println("Không tìm thấy người chơi với ID: " + room.getPlayerId2());
                        }
                        break;
                    }
                }

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
                        int result = register(username, password);
                        if(result == 1) {
                            response = "register_success";
                        } else if(result == -1) {
                            response = "register_failed_name_exist";
                        } else {
                            response = "register_failed";
                        }
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
                    case "update":
                        playerID = parts[1].split("=")[1];
                        String newUsername = parts[2].split("=")[1];
                        String newPassword = null;  // Khởi tạo newPassword là null
                        // Kiểm tra xem có password trong yêu cầu hay không
                        if (parts.length > 3 && parts[3].contains("password")) {
                            newPassword = parts[3].split("=")[1];  // Lấy password nếu có
                        }
                        String currentUsername = getCurrentUsername(playerID);
                        // Nếu tên đăng nhập mới giống với tên đăng nhập hiện tại, không cần kiểm tra
                        if (!newUsername.equals(currentUsername)) {
                            if (isUsernameExists(newUsername)) {
                                response = "error: username already exists";
                                break; // Kết thúc case nếu tên đăng nhập đã tồn tại
                            }
                        }
                        boolean updated = updatePlayerProfile(playerID, newUsername, newPassword);  // Gọi hàm cập nhật
                        response = updated ? "success: profile updated" : "error: update failed";
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
//                        System.out.println("Response to client: " + response);
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
//                        System.out.println("Response to client: " + response);
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
                        if(updateAccount(accountID, username, password, role)==1) {
                            response = "update success";
                        }
                        else if(updateAccount(accountID, username, password, role)==-1) {
                            response = "name existed";
                        }
                        else {
                            response = "update failure";
                        }
                        break;
                    case "addAccount":
                        username = parts[1].split("=")[1];
                        password = parts[2].split("=")[1];
                        role = parts[3].split("=")[1];
                        if(addAccount(username, password, role)==1) {
                            response = "add success";
                        } else if (addAccount(username, password, role)==-1) {
                            response ="name existed";
                        } else {
                            response = "add failure";
                        }
                        break;
                    case "getTrashs":
                        System.out.println("GetTrashs...");
                        List<TrashItem> trashItems = getListTrashs();
                        StringBuilder responseTrashs = new StringBuilder();
                        for (TrashItem a : trashItems) {
                            responseTrashs.append(String.format("name=%s&type=%s&img_url=%s|",
                                    a.getName(), a.getType(), a.getUrl()));
                        }
                        response = responseTrashs.toString();
                        System.out.println("Sucessed getTrashs");
                        break;

                    case "getBins":
                        System.out.println("GetBins...");
                        List<Bin> bins = getListBins();
                        StringBuilder responseBins = new StringBuilder();
                        for (Bin a : bins) {
                            responseBins.append(String.format("name=%s&type=%s&img_url=%s|",
                                    a.getName(), a.getType(), a.getUrl()));
                        }
                        response = responseBins.toString();
                        System.out.println("Sucessed getBins");
                        break;
                    case "getTrashItemData":
                        List<TrashItem> trashItems1 = getTrashItemData();
                        StringBuilder responseTrashItem = new StringBuilder();
                        for (TrashItem t : trashItems1) {
                            responseTrashItem.append(String.format("%s;%s;%s;%s;%s|",
                                    t.getId(), t.getName(), t.getType(), t.getUrl(),t.getDescription())); // Using semicolon as a delimiter
                        }
                        response = responseTrashItem.toString();
                        break;

                    case "getBinData":
                        List<Bin> bins1 = getBinData();
                        StringBuilder responseBin = new StringBuilder();
                        for (Bin b : bins1) {
                            responseBin.append(String.format("%s;%s;%s;%s;%s|",
                                    b.getId(), b.getName(), b.getType(), b.getUrl(),b.getDescription())); // Using semicolon as a delimiter
                        }
                        response = responseBin.toString();
                        break;
                    case "addTrashItem":

                        String name = parts[2].split("=")[1];
                        String kind = parts[3].split("=")[1];
                        String url = parts[4].split("=")[1];
                        String description = parts[5].split("=")[1];
                        if (addTrashItem(name, kind, url, description)) {
                            response = "add success";
                        } else {
                            response = "add failure";
                        }
                        break;

                    case "addBin":
                        name = parts[2].split("=")[1];
                        type = parts[3].split("=")[1];
                        url = parts[4].split("=")[1];
                        description = parts[5].split("=")[1];
                        if (addBin(name, type, url, description)) {
                            response = "add success";
                        } else {
                            response = "add failure";
                        }
                        break;

                    case "deleteTrashItem":
                        String id = parts[1].split("=")[1];
                        if (deleteTrashItem(id)) {
                            response = "delete success";
                        } else {
                            response = "delete failure";
                        }
                        break;

                    case "deleteBin":
                        id = parts[1].split("=")[1];
                        if (deleteBin(id)) {
                            response = "delete success";
                        } else {
                            response = "delete failure";
                        }
                        break;

                    case "updateTrashItem":
                        id = parts[1].split("=")[1];
                        name = parts[2].split("=")[1];
                        type = parts[3].split("=")[1];
                        url = parts[4].split("=")[1];
                        description = parts[5].split("=")[1];
                        if (updateTrashItem(id, name, type, url, description)) {
                            response = "update success";
                        } else {
                            response = "update failure";
                        }
                        break;

                    case "updateBin":
                        id = parts[1].split("=")[1];
                        name = parts[2].split("=")[1];
                        type = parts[3].split("=")[1];
                        url = parts[4].split("=")[1];
                        description = parts[5].split("=")[1];
                        if (updateBin(id, name, type, url, description)) {
                            response = "update success";
                        } else {
                            response = "update failure";
                        }
                        break;
                    case "setPlaying":
                        playerID = parts[1].split("=")[1];
                        setPlaying(playerID);
                        response = "success";
                        break;
                        case "getTrashTypes":
                        List<String> trashTypes = getTrashTypes();
                        StringBuilder responseTrashTypes = new StringBuilder();
                        for (String t : trashTypes) {
                            responseTrashTypes.append(String.format("%s;", t));
                        }
                        response = responseTrashTypes.toString();
                        break;
                    case "getOpponentPlayerID":
                        playerID = parts[1].split("=")[1];
                        String gameID = parts[2].split("=")[1];
                        String opponentPlayerID = getOpponentPlayerID(playerID, gameID);
                        response = "opponentPlayerID=" + opponentPlayerID;
                        break;
                    case "getOpponentAccountID":
                        playerID = parts[1].split("=")[1];
                        String opponentID = getOpponentAccountID(playerID);
                        response = "opponentID=" + opponentID;
                        break;
                    case "getOpponentName":
                        playerID = parts[1].split("=")[1];
                        String opponentName = getOpponentName(playerID);
                        response = "opponentName=" + opponentName;
                        break;
                    case "getGameScore":
                        playerID = parts[1].split("=")[1];
                        gameID = parts[2].split("=")[1];
                        int score = getGameScore(playerID, gameID);
                        response = "score=" + score;
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
    public static String getOpponentPlayerID(String playerID, String gameID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT playerID FROM player_game WHERE gameID = ? AND playerID != ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, gameID);
                stmt.setString(2, playerID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("playerID");
                }
                return "error: opponent not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: server error";
        }
    }
    public static String getOpponentAccountID(String playerID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT accountID FROM player WHERE playerID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("accountID");
                }
                return "error: opponent not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: server error";
        }
    }
    public static String getOpponentName(String accountID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT username FROM account WHERE accountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("username");
                }
                return "error: opponent not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error: server error";
        }
    }

    private static int getGameScore(String playerID, String gameID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT score FROM player_game WHERE playerID = ? AND gameID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                stmt.setString(2, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("score");
                }
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



    private static List<String> getTrashTypes() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT DISTINCT type FROM trashbin";
            List<String> trashTypes = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    trashTypes.add(rs.getString("type"));
                }
                return trashTypes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
    private static int register(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if username already exists
            String checkQuery = "SELECT * FROM account WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) return -1;
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
            return 0;
        }
    }

    // Create a new player linked to an account
    private static int createPlayer(String accountID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String playerID = UUID.randomUUID().toString();
            String insertPlayerQuery = "INSERT INTO player (playerID, accountID, total_games, total_wins, total_score, average_score, created_at, updated_at) VALUES (?, ?, 0, 0, 0, 0, NOW(), NOW())";
            try (PreparedStatement insertPlayerStmt = conn.prepareStatement(insertPlayerQuery)) {
                insertPlayerStmt.setString(1, playerID);
                insertPlayerStmt.setString(2, accountID);
                insertPlayerStmt.executeUpdate();
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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

    // get list trashs
    private static List<TrashItem> getListTrashs() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT t.name, t.type, t.img_url "
                    + "FROM trashitem t";
            List<TrashItem> listTrashs = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String img_url = rs.getString("img_url"); // Lấy timestamp cho created_at

                    // Trả về đối tượng Player với các thông tin đã lấy
                    listTrashs.add(new TrashItem(name, type, img_url));
                }
                return listTrashs;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Hoặc trả về một đối tượng lỗi nếu cần
        }
    }

    // get list bins
    private static List<Bin> getListBins() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT t.name, t.type, t.img_url "
                    + "FROM trashbin t";
            List<Bin> listBins = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String img_url = rs.getString("img_url"); // Lấy timestamp cho created_at

                    // Trả về đối tượng Player với các thông tin đã lấy
                    listBins.add(new Bin(name, type, img_url));
                }
                return listBins;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Hoặc trả về một đối tượng lỗi nếu cần
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

    private static String getCurrentUsername(String playerID) {
        String username = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT a.username FROM account a " +
                    "JOIN player p ON a.accountID = p.accountID " +
                    "WHERE p.playerID = ?"; // Sử dụng playerID để tìm kiếm
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID); // Giả sử playerID là chuỗi. Nếu là số, hãy dùng setInt
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
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

    private static boolean updatePlayerProfile(String playerID, String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Câu lệnh cập nhật username và password cho người chơi
            String query = "UPDATE account SET username = ?, password = ? " +
                    "WHERE accountID = (SELECT accountID FROM player WHERE playerID = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, playerID); // Giả sử playerID là chuỗi. Nếu là số, hãy dùng setInt
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;  // Trả về true nếu có ít nhất 1 dòng được cập nhật
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
                    + "WHERE p.status = 1 AND p.isPlaying = 0 AND p.playerID != ? AND a.role = 'player' "; // Lọc ra bạn bè online và không phải là bản thân

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
            String query = "SELECT * FROM account ORDER BY username";
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
    private static int updateAccount(String accountID, String username, String password, String role) {
        String oldUsername = getAccountbyID(accountID).split("&")[0].split("=")[1];
        // Check if the old username matches the new username or if the new username is unique
        if (oldUsername.equals(username) || !isUsernameExists(username)) {
            return updateAccountInDB(accountID, username, password, role);
        } else {
            // Username already exists, return error code
            return -1;
        }
    }

    // Method to perform the database update for the account
    private static int updateAccountInDB(String accountID, String username, String password, String role) {
        String query = "UPDATE account SET username = ?, password = ?, role = ?, updated_at = NOW() WHERE accountID = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setString(4, accountID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? 1 : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private static int addAccount(String username, String password, String role) {
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (isUsernameExists(username)) {
            return -1; // Tên đăng nhập đã tồn tại, không thêm tài khoản
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String accountID = UUID.randomUUID().toString();
            String query = "INSERT INTO account (accountID, username, password, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountID);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.setString(4, role);

                // Execute the insert statement
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    return createPlayer(accountID); // Call to createPlayer only if account creation was successful
                } else {
                    return 0; // Failed to insert account
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Handle exceptions accordingly
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
    private static List<TrashItem> getTrashItemData() {
        List<TrashItem> trashItems = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM trashitem ORDER BY type";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String id = rs.getString("itemID");
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String url = rs.getString("img_url");
                    String description = rs.getString("description");
                    trashItems.add(new TrashItem(id, name, type, url,description));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trashItems;
    }
    private static List<Bin> getBinData() {
        List<Bin> bins = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM trashbin ORDER BY type";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String id = rs.getString("binID");
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String url = rs.getString("img_url");
                    String description = rs.getString("description");
                    bins.add(new Bin(id, name, type, url,description));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bins;
    }
    public static boolean addTrashItem(String name, String kind, String url, String description) {
        System.out.println("Adding trash item");
        System.out.println(name + " " + kind + " " + url + " " + description);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String itemID = UUID.randomUUID().toString();
            String query = "INSERT INTO trashitem (itemID, name, type, img_url, description) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, itemID);   // itemID
                stmt.setString(2, name);      // name
                stmt.setString(3, kind);      // type
                stmt.setString(4, url);       // img_url
                stmt.setString(5, description); // description

                System.out.println(stmt);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addBin(String name, String type, String url, String description) {
        if (!addEnumType("trashbin", "type", type)) {
            System.err.println("Không thể thêm giá trị mới vào ENUM type.");
            return false;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String binID = UUID.randomUUID().toString();
            String query = "INSERT INTO trashbin (binID, name, type, img_url, description) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, binID);
                stmt.setString(2, name);
                stmt.setString(3, type);
                stmt.setString(4, url);
                stmt.setString(5, description);
                System.out.println(stmt);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm để thêm giá trị ENUM mới vào cột nếu chưa tồn tại
    private static boolean addEnumType(String tableName, String columnName, String enumValue) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Lấy các giá trị ENUM hiện tại của cột
            String enumQuery = "SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'";
            ResultSet rs = stmt.executeQuery(enumQuery);

            if (rs.next()) {
                String enumDefinition = rs.getString("Type");

                // Kiểm tra nếu enumValue đã tồn tại trong danh sách
                if (enumDefinition.contains("'" + enumValue + "'")) {
                    return true; // Giá trị đã tồn tại
                }

                // Bỏ "ENUM(" và ")" khỏi định nghĩa hiện tại để chuẩn bị thêm giá trị mới
                String existingEnums = enumDefinition.substring(enumDefinition.indexOf("(") + 1, enumDefinition.lastIndexOf(")"));
                String newEnumDefinition = "ENUM(" + existingEnums + ", '" + enumValue + "')";

                // Tạo câu lệnh ALTER TABLE với giá trị mới
                String alterQuery = "ALTER TABLE " + tableName + " MODIFY " + columnName + " " + newEnumDefinition;

                // Thực hiện câu lệnh ALTER TABLE
                stmt.execute(alterQuery);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static boolean deleteTrashItem(String itemID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM trashitem WHERE itemID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, itemID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteBin(String binID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM trashbin WHERE binID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, binID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateTrashItem(String itemID, String name, String type, String url, String description) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE trashitem SET name = ?, type = ?, img_url = ? , description = ? WHERE itemID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setString(3, url);
                stmt.setString(4, description);
                stmt.setString(5, itemID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateBin(String binID, String name, String type, String url, String description) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE trashbin SET name = ?, type = ?, img_url = ?, description = ? WHERE binID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, type);
                stmt.setString(3, url);
                stmt.setString(4, description);
                stmt.setString(5, binID);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void setPlaying(String playerID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE player SET isPlaying = 0 WHERE playerID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerID);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
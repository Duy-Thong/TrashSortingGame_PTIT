package Server;

import Server.model.PlayHistoryDTO;
import Server.model.RankPlayerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper {
    public static boolean authenticate(String username, String password) {
        try (Connection conn = DatabaseConnection.dbGetConnection()) {
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

    public static boolean register(String username, String password) {
        try (Connection conn = DatabaseConnection.dbGetConnection()) {
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

    public static boolean createPlayer(String accountID) {
        try (Connection conn = DatabaseConnection.dbGetConnection()) {
            // Generate a new playerID
            String playerID = UUID.randomUUID().toString();

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

    public static List<RankPlayerDTO> getRankPlayer() {
        List<RankPlayerDTO> rankPlayerDTOS = new ArrayList<>();
        String query = "SELECT account.username,player.total_games,player.total_wins,player.total_score FROM account INNER JOIN player ON account.accountID= player.accountID ORDER BY player.total_wins DESC";

        try (Connection connection = DatabaseConnection.dbGetConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int totalGames = resultSet.getInt("total_games");
                int totalWins = resultSet.getInt("total_wins");
                int totalScore = resultSet.getInt("total_score");

                RankPlayerDTO rankPlayerDTO = new RankPlayerDTO( username, totalGames, totalWins, totalScore);
                rankPlayerDTOS.add(rankPlayerDTO);
            }
            return rankPlayerDTOS;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rankPlayerDTOS;
    }

    public static List<PlayHistoryDTO> getPlayHistory(String playerID1) {
        List<PlayHistoryDTO> playHistoryDTOS = new ArrayList<>();

        String query = "SELECT account.username, player_game.join_time, player_game.play_duration, player_game.score " +
                "FROM player_game " +
                "INNER JOIN player ON player_game.playerID = player.playerID " +
                "INNER JOIN account ON account.accountID = player.accountID " +
                "WHERE player_game.playerID = ?";

        try (Connection connection = DatabaseConnection.dbGetConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerID1);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    Date join_time = resultSet.getDate("join_time");
                    int play_duration = resultSet.getInt("play_duration");
                    int score = resultSet.getInt("score");

                    PlayHistoryDTO playHistoryDTO = new PlayHistoryDTO(username, join_time, play_duration, score);
                    playHistoryDTOS.add(playHistoryDTO);
                }
            }
            return playHistoryDTOS;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return playHistoryDTOS;
    }


}
package Server.model;

import java.sql.Timestamp;

public class Player {
    private String playerID;
    private String username;
    private int totalGames;
    private int totalWins;
    private int totalScore;
    private int averageScore;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public Player(String playerID, String username, int totalGames, int totalWins, int totalScore, int averageScore, Timestamp createdAt, Timestamp updatedAt) {
        this.playerID = playerID;
        this.username = username;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.totalScore = totalScore;
        this.averageScore = averageScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

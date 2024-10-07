package Server.model;

public class Player {
    private String username;
    private int totalGames;
    private int totalWins;
    private int totalScore;
    private int averageScore;

    // Constructor
    public Player(String username, int totalGames, int totalWins, int totalScore, int averageScore) {
        this.username = username;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.totalScore = totalScore;
        this.averageScore = averageScore;
    }

    // Getters and Setters
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
}
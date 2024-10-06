package Server.model;

import java.util.Date;

public class RankPlayerDTO {
    private String username;    // varchar(36)
    private int total_game;    // varchar(36)
    private int total_wins;      // int(11)
    private int total_score;       // int(11)

    public RankPlayerDTO(String username, int total_game, int total_wins, int total_score) {
        this.username = username;
        this.total_game = total_game;
        this.total_wins = total_wins;
        this.total_score = total_score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotal_game() {
        return total_game;
    }

    public void setTotal_game(int total_game) {
        this.total_game = total_game;
    }

    public int getTotal_wins() {
        return total_wins;
    }

    public void setTotal_wins(int total_wins) {
        this.total_wins = total_wins;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    @Override
    public String toString() {
        return "RankPlayerDTO{" +
                "username='" + username + '\'' +
                ", total_game=" + total_game +
                ", total_wins=" + total_wins +
                ", total_score=" + total_score +
                '}';
    }
}
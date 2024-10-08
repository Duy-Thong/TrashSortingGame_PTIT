package Client.model;

import java.time.LocalDateTime;

public class PlayerGame {
    private String playerID;
    private String gameID;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
    private Integer playDuration; // in seconds
    private int score;
    private String result; // 'win', 'lose', 'draw'
    private boolean isFinal;

    public PlayerGame() {}

    public PlayerGame(String playerID, String gameID, LocalDateTime joinTime, LocalDateTime leaveTime, Integer playDuration, int score, String result, boolean isFinal) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.joinTime = joinTime;
        this.leaveTime = leaveTime;
        this.playDuration = playDuration;
        this.score = score;
        this.result = result;
        this.isFinal = isFinal;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public LocalDateTime getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(LocalDateTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public Integer getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(Integer playDuration) {
        this.playDuration = playDuration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}

package Server.model;

import java.time.LocalDateTime;
import java.util.List;

public class Game {
    private String gameID;
    private String status; // 'pending', 'active', 'finished'
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalScore;
    private List<String> listTrashBin;
    private List<String> listTrashItem;

    public Game() {}

    public Game(String gameID, String status, LocalDateTime startTime, LocalDateTime endTime, int totalScore, List<String> listTrashBin, List<String> listTrashItem) {
        this.gameID = gameID;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalScore = totalScore;
        this.listTrashBin = listTrashBin;
        this.listTrashItem = listTrashItem;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public List<String> getListTrashBin() {
        return listTrashBin;
    }

    public void setListTrashBin(List<String> listTrashBin) {
        this.listTrashBin = listTrashBin;
    }

    public List<String> getListTrashItem() {
        return listTrashItem;
    }

    public void setListTrashItem(List<String> listTrashItem) {
        this.listTrashItem = listTrashItem;
    }
}


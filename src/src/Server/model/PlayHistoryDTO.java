package Server.model;

import java.util.Date;

public class PlayHistoryDTO {
    String username;
    Date join_time;
    int play_duration;
    int score;

    public PlayHistoryDTO(String username, Date join_time, int play_duration, int score) {
        this.username = username;
        this.join_time = join_time;
        this.play_duration = play_duration;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getJoin_time() {
        return join_time;
    }

    public void setJoin_time(Date join_time) {
        this.join_time = join_time;
    }

    public int getPlay_duration() {
        return play_duration;
    }

    public void setPlay_duration(int play_duration) {
        this.play_duration = play_duration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "PlayHistoryDTO{" +
                "username='" + username + '\'' +
                ", join_time=" + join_time +
                ", play_duration=" + play_duration +
                ", score=" + score +
                '}';
    }
}

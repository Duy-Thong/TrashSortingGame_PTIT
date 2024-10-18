package Server.model;

public class Room {
    private String roomId;
    private String playerId1;
    private String playerId2;

    public Room(String roomId, String playerId1, String playerId2) {
        this.roomId = roomId;
        this.playerId1 = playerId1;
        this.playerId2 = playerId2;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(String playerId1) {
        this.playerId1 = playerId1;
    }

    public String getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(String playerId2) {
        this.playerId2 = playerId2;
    }
}

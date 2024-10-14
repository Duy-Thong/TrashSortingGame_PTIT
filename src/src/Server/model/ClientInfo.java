package Server.model;

import java.net.InetAddress;

public class ClientInfo {
    private String accountID;
    private String playerID;
    private InetAddress address;
    private int port;

    public ClientInfo(String accountID, String playerID, InetAddress address, int port) {
        this.accountID = accountID;
        this.playerID = playerID;
        this.address = address;
        this.port = port;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getPlayerID() {
        return playerID;

    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
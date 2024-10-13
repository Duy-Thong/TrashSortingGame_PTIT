package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public UDPClient(String serverIp, int port) throws Exception {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(serverIp);
        serverPort = port;
    }

    // Cập nhật phương thức gửi thông điệp điểm
    public void sendScoreUpdate(int playerId, int newScore, int roomId) {
        try {
            String message = "UPDATE_SCORE:" + playerId + ":" + newScore + ":" + roomId;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

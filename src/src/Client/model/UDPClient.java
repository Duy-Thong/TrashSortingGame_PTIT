package Client.model;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Random;

public class UDPClient {
    private DatagramSocket socket;
    private DatagramSocket socketListen;
    private InetAddress serverAddress;
    private int serverPort;
    private updateUI mUpdateUI;
    public UDPClient(String serverIp, int port) throws Exception {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(serverIp);
        serverPort = port;
    }

    // Cập nhật phương thức gửi thông điệp điểm
    public void sendScoreUpdate(String playerId, int newScore, String roomId) {
        try {
            String message = "type=UPDATE_SCORE&" +"playerId="+ playerId + "&" + "newScore="+ newScore + "&" + "roomId=" + roomId;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Sent to sv: " + message + " to " + serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức lắng nghe phản hồi từ server (chạy trong luồng riêng)
    public void listenForResponses() {
        // Sử dụng thread riêng để không bị khóa chương trình
        new Thread(() -> {
            while (true) {
                try (DatagramSocket socket = new DatagramSocket(12349)) {
                    byte[] receiveBuffer = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket); // Nhận phản hồi từ server
                    socketListen = socket;
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received: " + response);
                    if(response.equals("type=end_socket")) {
                        socket.close();
                        break;
                    }
                    else {
                        // Gọi update UI nếu có phản hồi liên quan đến điểm số
                        if (mUpdateUI != null) {
                            mUpdateUI.updateScorePlayer(); // Cập nhật UI
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Bắt đầu luồng lắng nghe
    }

    public interface updateUI{
        void updateScorePlayer();
    }


    public updateUI getmUpdateUI() {
        return mUpdateUI;
    }

    public void setmUpdateUI(updateUI mUpdateUI) {
        this.mUpdateUI = mUpdateUI;
    }

    // Cập nhật player_game
    public void sendScoreUpdatePlayerGame(String playerId, String gameId, int score, String result) {
        try {
            String message = "type=update_player_game&" +"playerId="+ playerId + "&" + "gameId=" + gameId + "&" + "score=" + score + "&" + "result=" + result ;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Sent to sv: " + message + " to " + serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePlayer(String playerId, int score, String result) {
        try {
            String message = "type=update_player&" +"playerId="+ playerId + "&" + "score=" + score + "&" + "result=" + result ;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Sent to sv: " + message + " to " + serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendUpdateGame(String gameID, int score) {
        try {
            String message = "type=update_game&" +"gameID="+ gameID + "&" + "score=" + score;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Sent to sv: " + message + " to " + serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void endSocket() {
        socketListen.close();
    }
}

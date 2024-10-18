package Client.model;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private DatagramSocket socket;
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
            String message = "type=UPDATE_SCORE&" + playerId + "&" + newScore + "&" + roomId;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Sent " + message + " to " + serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức lắng nghe phản hồi từ server (chạy trong luồng riêng)
    public void listenForResponses() {
        // Sử dụng thread riêng để không bị khóa chương trình
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket); // Nhận phản hồi từ server

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received: " + response);

                    // Gọi update UI nếu có phản hồi liên quan đến điểm số
                    if (mUpdateUI != null) {
                        mUpdateUI.updateScorePlayer(); // Cập nhật UI
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
}

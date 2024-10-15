package Client.controller;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class InviteManager {
    private static InviteManager instance;
    private static final String SERVER_ADDRESS = "26.29.9.206";
    private static final int SERVER_PORT = 12345;

    private DatagramSocket socket;

    private InviteManager() {
        try {
            socket = new DatagramSocket(); // Không chỉ định cổng
        } catch (Exception e) {
            e.printStackTrace();
        }
        listenForInvitations();
    }

    public static synchronized InviteManager getInstance() {
        if (instance == null) {
            instance = new InviteManager();
        }
        return instance;
    }

    public void listenForInvitations() {
        new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Sử dụng socket toàn cục

                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    String[] parts = receivedMessage.split("&");

                    if (parts[0].equals("type=invited")) {
                        String senderId = parts[1].split("=")[1];
                        String receiverId = parts[2].split("=")[1];

                        // Hiển thị popup mời chơi
                        showInvitePopup(senderId, receiverId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showInvitePopup(String senderId, String receiverId) {
        SwingUtilities.invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(null,
                    "Người chơi " + senderId + " mời bạn tham gia trò chơi. Bạn có chấp nhận không?",
                    "Lời mời chơi game", JOptionPane.YES_NO_OPTION);

            String replyMessage = (response == JOptionPane.YES_OPTION)
                    ? "invite_response;from=" + receiverId + ";status=accepted"
                    : "invite_response;from=" + receiverId + ";status=declined";

            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                byte[] sendData = replyMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket); // Gửi qua socket toàn cục
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}


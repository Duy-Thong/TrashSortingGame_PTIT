package Client.controller;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.ServerSocket;

public class InviteService implements Runnable {
    private DatagramSocket invitationSocket;
    private static final String SERVER_ADDRESS = "localhost"; // Thay đổi địa chỉ máy chủ nếu cần
    private static final int SERVER_PORT = 12345; // Thay đổi cổng nếu cần

    public InviteService() {
        try {
            // Tìm một cổng chưa được sử dụng
            int port = findAvailablePort();
            invitationSocket = new DatagramSocket(port);
            System.out.println("Listening for invitations on port: " + port);
        } catch (SocketException e) {
            System.err.println("Failed to create DatagramSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Lắng nghe lời mời ở đây
        while (invitationSocket != null && !invitationSocket.isClosed()) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                invitationSocket.receive(packet);
                String invitation = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received invitation: " + invitation);
                // Xử lý lời mời tại đây
                processInvitation(invitation);
            } catch (IOException e) {
                System.err.println("Error receiving invitation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processInvitation(String invitation) {
        String[] parts = invitation.split(";");
        if (parts.length >= 3 && parts[0].equals("type=invited")) {
            String senderId = parts[1].split("=")[1];
            String receiverId = parts[2].split("=")[1];

            // Hiển thị popup mời chơi
            showInvitePopup(senderId, receiverId);
        }
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
                invitationSocket.send(sendPacket); // Gửi qua socket toàn cục
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private int findAvailablePort() {
        for (int port = 49152; port <= 65535; port++) { // Cổng động
            if (!isPortInUse(port)) {
                return port;
            }
        }
        throw new RuntimeException("No available ports found");
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false; // Port chưa được sử dụng
        } catch (IOException e) {
            return true; // Port đã được sử dụng
        }
    }

    public void close() {
        if (invitationSocket != null && !invitationSocket.isClosed()) {
            invitationSocket.close();
            System.out.println("Invitation socket closed.");
        }
    }
}


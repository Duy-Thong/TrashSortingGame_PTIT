package Client.controller;

import Client.model.Player;

import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class InviteController {
    private static final String SERVER_ADDRESS = "26.29.9.206";
    private static final int SERVER_PORT = 12345;

    private List<Player> availablePlayers;
    private long inviteTimeout = 60000;

    // Hàm lấy danh sách người chơi có status = 1 và isPlaying = 0 từ server
    public void getListFriends(String playerID, AvailablePlayersCallback callback) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=friends&playerID=" + playerID;
            byte[] buffer = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            byte[] responseBuffer = new byte[2048];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] playerDataList = response.split("\\|");

            availablePlayers = new ArrayList<>();

            for (String record : playerDataList) {
                String[] playerData = record.split("&");
                if (playerData.length == 9) {
                    String friendID = playerData[0].split("=")[1];
                    String username = playerData[1].split("=")[1];
                    int totalGames = Integer.parseInt(playerData[2].split("=")[1]);
                    int totalWins = Integer.parseInt(playerData[3].split("=")[1]);
                    int totalScore = Integer.parseInt(playerData[4].split("=")[1]);
                    int averageScore = Integer.parseInt(playerData[5].split("=")[1]);
                    int status = Integer.parseInt(playerData[6].split("=")[1]);
                    int isPlaying = Integer.parseInt(playerData[7].split("=")[1]);
                    Timestamp createdAt = Timestamp.valueOf(playerData[8].split("=")[1]);

                    availablePlayers.add(new Player(friendID, username, totalGames, totalWins, totalScore, averageScore, status, isPlaying, createdAt));
                }
            }

            if (callback != null) {
                callback.onAvailablePlayersReceived(availablePlayers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu mời người chơi
    public void invitePlayer(String currentPlayerID, String invitedPlayerID, InviteCallback callback) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=invite&playerID=" + currentPlayerID + "&invitedPlayerID=" + invitedPlayerID;
            byte[] buffer = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Hiển thị thông báo rằng lời mời đã được gửi
            JOptionPane.showMessageDialog(null, "Lời mời đã được gửi đến " + invitedPlayerID + "!");

            // Bắt đầu đếm ngược thời gian chờ
            Timer timer = new Timer((int) inviteTimeout, e -> {
                callback.onInviteTimeout(invitedPlayerID);
            });
            timer.setRepeats(false);
            timer.start();

            // Nhận phản hồi đồng bộ
            socket.setSoTimeout((int) inviteTimeout); // Thiết lập thời gian chờ
            try {
                byte[] responseBuffer = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(responsePacket); // Chờ nhận phản hồi

                String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                if (responseMessage.equals("accepted")) {
                    callback.onInviteAccepted(invitedPlayerID);
                } else if (responseMessage.equals("declined")) {
                    callback.onInviteDeclined(invitedPlayerID);
                }

                // Dừng bộ đếm thời gian khi nhận được phản hồi
                timer.stop();
            } catch (SocketTimeoutException e) {
                // Xử lý trường hợp hết thời gian chờ mà không nhận được phản hồi
                callback.onInviteTimeout(invitedPlayerID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Luồng lắng nghe lời mời
    static class InviteListener implements Runnable {
        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket(12346)) {
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Nhận gói tin từ server

                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    String[] parts = receivedMessage.split("&"); // Phân tách thông điệp

                    if (parts[0].equals("type=invited")) {
                        String senderId = parts[1].split("=")[1]; // ID người gửi
                        String receiverId = parts[2].split("=")[1]; // ID người nhận

                        // Hiển thị popup mời chơi
                        showInvitePopup(senderId, receiverId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức hiển thị popup lời mời
    public static void showInvitePopup(String senderId, String receiverId) {
        SwingUtilities.invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(null,
                    "Người chơi " + senderId + " mời bạn tham gia trò chơi. Bạn có chấp nhận không?",
                    "Lời mời chơi game", JOptionPane.YES_NO_OPTION);

            String replyMessage = (response == JOptionPane.YES_OPTION)
                    ? "invite_response;from=" + receiverId + ";status=accepted"
                    : "invite_response;from=" + receiverId + ";status=declined";

            try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)){
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                byte[] sendData = replyMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Callback interface cho danh sách người chơi
    public interface AvailablePlayersCallback {
        void onAvailablePlayersReceived(List<Player> players);
    }

    // Callback interface cho việc mời bạn
    public interface InviteCallback {
        void onInviteTimeout(String playerID);
        void onInviteAccepted(String playerID);
        void onInviteDeclined(String playerID);
    }
}

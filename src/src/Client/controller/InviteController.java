package Client.controller;

import Client.model.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane; // Thêm import để sử dụng JOptionPane
import javax.swing.Timer;

public class InviteController {
    private static final String SERVER_ADDRESS = "26.29.9.206"; // Địa chỉ server
    private static final int SERVER_PORT = 12345; // Cổng server

    private List<Player> availablePlayers; // Danh sách bạn bè có sẵn
    private long inviteTimeout = 30000; // Thời gian mời (30 giây)

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
                    String friendID = playerData[0].split("=")[1]; // Đổi tên biến thành friendID
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

            // Bắt đầu đếm ngược thời gian chờ
            Timer timer = new Timer((int) inviteTimeout, e -> {
                callback.onInviteTimeout(invitedPlayerID);
            });
            timer.setRepeats(false);
            timer.start();

            // Hiển thị thông báo rằng lời mời đã được gửi
            JOptionPane.showMessageDialog(null, "Lời mời đã được gửi đến " + invitedPlayerID + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm nhận lời mời từ server
    public void receiveInvite(String currentPlayerID) {
        try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) { // Lắng nghe lời mời trên cổng
            byte[] responseBuffer = new byte[2048];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            while (true) { // Vòng lặp vô hạn để liên tục lắng nghe lời mời
                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                handleReceivedInvite(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xử lý lời mời nhận được
    private void handleReceivedInvite(String response) {
        String[] parts = response.split("&");
        String invitedPlayerID = parts[1].split("=")[1];
        showInvitationPopup(invitedPlayerID);
    }

    private void showInvitationPopup(String invitedPlayerID) {
        int response = JOptionPane.showConfirmDialog(null, invitedPlayerID + " đã mời bạn chơi game!",
                "Lời mời chơi game", JOptionPane.YES_NO_OPTION);

        // Kiểm tra phản hồi của người chơi
        if (response == JOptionPane.YES_OPTION) {
            // Thực hiện hành động khi chấp nhận lời mời
            // Callback có thể được sử dụng ở đây nếu cần thiết
            System.out.println(invitedPlayerID + " đã chấp nhận lời mời!");
        } else {
            System.out.println(invitedPlayerID + " đã từ chối lời mời!");
        }
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
package Client.controller;

import Client.Constants;
import Client.model.Player;
import Client.view.RunGame;

import java.net.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class InviteController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT;
    private static final int CLIENT_PORT = 12344;

    private List<Player> availablePlayers;
    private int inviteTimeout = 5000;
    private static RunGame game;

    private String username;

    public InviteController(String username) {
        this.username = username;
    }

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
    public void invitePlayer(String currentPlayerID, String invitedPlayerID, String invitedPlayerName, InviteCallback callback) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (DatagramSocket socket = new DatagramSocket(CLIENT_PORT)) {
                    String message = "type=invite&playerID=" + currentPlayerID + "&invitedPlayerID=" + invitedPlayerID + "&invitePlayerName=" + username;
                    byte[] buffer = message.getBytes();
                    InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);

                    // Gửi lời mời
                    socket.send(packet);
                    System.out.println("Invitation sent to " + invitedPlayerID);
                    // Đặt thời gian chờ cho việc nhận
                    socket.setSoTimeout(inviteTimeout);
                    System.out.println("Waiting for response for " + inviteTimeout + " milliseconds...");

                    try {
                        byte[] receiveBuffer = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(receivePacket);

                        // Xử lý phản hồi từ server
                        String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Received response: " + responseMessage);

                        String[] parts = responseMessage.split("&");

                        if (parts[0].equals("type=accepted")) {
                            String gameId = parts[1].split("=")[1];
                            String senderId = parts[2].split("=")[1];
                            String receiverId = parts[3].split("=")[1];
                            Timestamp startTime = Timestamp.valueOf(parts[4].split("=")[1]);
                            System.out.println("Game ID: " + gameId + ", Sender ID: " + senderId + ", Receiver ID: " + receiverId+", Start time: "+startTime);
                            callback.onInviteAccepted(invitedPlayerID, gameId);
                        } else if (parts[0].equals("type=declined")) {
                            System.out.println("Invite declined by " + invitedPlayerID);
                            callback.onInviteDeclined(invitedPlayerID);
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("No response from " + invitedPlayerID + " after timeout.");
                        callback.onInviteTimeout(invitedPlayerID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Đây là nơi bạn có thể cập nhật lại UI sau khi công việc nền hoàn thành, nếu cần.
            }
        }.execute();
    }


    // Luồng lắng nghe lời mời
    static class InviteListener implements Runnable {
        @Override
        public void run() {
            int port = 12346;
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("Client đang lắng nghe trên cổng: "+ port);
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    String[] parts = receivedMessage.split("&");

                    if (parts[0].equals("type=invited")) {
                        String senderId = parts[1].split("=")[1];
                        String receiverId = parts[2].split("=")[1];
                        String senderName = parts[3].split("=")[1];

                        // Hiển thị popup mời chơi
                        showInvitePopup(senderId, receiverId, senderName);
                    }
                    else {
                        String gameId = parts[1].split("=")[1];
                        String senderId = parts[2].split("=")[1];
                        String receiverId = parts[3].split("=")[1];
                        Timestamp startTime = Timestamp.valueOf(parts[4].split("=")[1]);
                        System.out.println("Game ID: " + gameId + ", Sender ID: " + senderId + ", Receiver ID: " + receiverId + ", Start time: " + startTime);

                        // Calculate the delay until startTime
                        long delay = startTime.getTime() - System.currentTimeMillis();

                        if (delay > 0) {
                            // Schedule the game to start after the delay
                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                            scheduler.schedule(() -> {
                                // Ensure RunGame is executed on the EDT
                                SwingUtilities.invokeLater(() -> {
                                    new RunGame(gameId, receiverId, senderId).setVisible(true);
                                });
                            }, delay, TimeUnit.MILLISECONDS);
                            scheduler.shutdown(); // Shutdown the scheduler after scheduling the task
                        } else {
                            // If the start time is in the past, start the game immediately
                            SwingUtilities.invokeLater(() -> {
                                new RunGame(gameId, receiverId, senderId).setVisible(true);
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức hiển thị popup lời mời
    public static void showInvitePopup(String senderId, String receiverId, String senderName) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Displaying popup for invite from: " + senderId);

            // Tùy chỉnh các nút và văn bản
            Object[] options = {"Đồng ý", "Từ chối"};
            int response = JOptionPane.showOptionDialog(null,
                    "Người chơi " + senderName + " mời bạn tham gia trò chơi. Bạn có chấp nhận không?",
                    "Lời mời chơi game",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]); // options[0] là nút mặc định "Đồng ý"

            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                // Chỉnh sửa message để gửi
                String message = "type=response&senderID=" + senderId + "&receiverId=" + receiverId + "&status=" + (response == JOptionPane.YES_OPTION ? "accepted" : "declined");
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
                System.out.println("Sent response: " + message);
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
        void onInviteAccepted(String playerID, String roomId);
        void onInviteDeclined(String playerID);
    }
}
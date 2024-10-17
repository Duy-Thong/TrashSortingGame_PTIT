package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;

import Client.model.Player;

public class ProfileController {
    private static final String SERVER_ADDRESS = "26.29.9.209"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;

    public Player getPlayerProfile(String playerID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=profile&playerID=playerID"
            String message = "type=profile&playerID=" + playerID;
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Xử lý phản hồi từ server
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] playerData = response.split("&");

            // Kiểm tra xem dữ liệu trả về có đúng định dạng không
            if (playerData.length == 9) {  // Nếu có đầy đủ các trường dữ liệu
                String playerId = playerData[0].split("=")[1];
                String username = playerData[1].split("=")[1];
                int totalGames = Integer.parseInt(playerData[2].split("=")[1]);
                int totalWins = Integer.parseInt(playerData[3].split("=")[1]);
                int totalScore = Integer.parseInt(playerData[4].split("=")[1]);
                int averageScore = Integer.parseInt(playerData[5].split("=")[1]);
                int status = Integer.parseInt(playerData[6].split("=")[1]);
                int isPlaying = Integer.parseInt(playerData[7].split("=")[1]);

                // Chuyển đổi chuỗi thành Timestamp
                Timestamp createdAt = Timestamp.valueOf(playerData[8].split("=")[1]);  // Parse string to Timestamp

                return new Player(playerId, username, totalGames, totalWins, totalScore, averageScore, status, isPlaying, createdAt);

            } else {
                System.out.println("Invalid response from server.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
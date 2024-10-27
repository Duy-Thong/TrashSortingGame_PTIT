package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;

import Client.Constants;
import Client.model.Player;

public class ProfileController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT;

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

    // Hàm cập nhật thông tin người chơi trên server
    public String updatePlayerProfile(String playerID, String newUsername, String newPassword) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp yêu cầu cập nhật
            String message = "type=update&playerID=" + playerID + "&username=" + newUsername;
            if (!newPassword.isEmpty()) {
                message += "&password=" + newPassword;  // Chỉ gửi password nếu có nhập
            }
            System.out.println(message);
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            // Xử lý phản hồi
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            return response;  // Trả về phản hồi từ server
        } catch (Exception e) {
            e.printStackTrace();
            return "error: update failed";  // Trả về lỗi nếu có ngoại lệ
        }
    }
}


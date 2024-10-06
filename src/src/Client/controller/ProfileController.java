package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Client.model.Player;

public class ProfileController {
    private static final String SERVER_ADDRESS = "localhost"; // Thay đổi nếu server chạy trên máy khác
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
            if (playerData.length == 5) {

                // Tạo một đối tượng Player từ phản hồi
                String username = playerData[0].split("=")[1]; // Lấy username
                int totalGames = Integer.parseInt(playerData[1].split("=")[1]); // Lấy totalGames
                int totalWins = Integer.parseInt(playerData[2].split("=")[1]); // Lấy totalWins
                int totalScore = Integer.parseInt(playerData[3].split("=")[1]); // Lấy totalScore
                int averageScore = Integer.parseInt(playerData[4].split("=")[1]); // Lấy averageScore
                // Lưu ý: Nếu có createdAt, hãy thêm vào model Player nếu cần thiết

                // Đóng gói dữ liệu vào model Player
                Player player = new Player(
                        username,  // Username
                        totalGames,  // Total Games
                        totalWins,  // Total Wins
                        totalScore,  // Total Score
                        averageScore  // Average Score
                        // Nếu có createdAt thì thêm tham số này
                );

                // Trả về đối tượng Player sau khi đã đóng gói
                return player;
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
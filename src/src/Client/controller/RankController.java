package Client.controller;

import Client.model.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RankController {
    private static final String SERVER_ADDRESS = "26.29.9.209"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;

    // Hàm lấy danh sách top người chơi từ server
    public List<Player> getRank() {
        List<Player> topPlayers = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp gửi đến server, ví dụ: "type=rank"
            String message = "type=rank";
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[2048]; // Kích thước buffer để chứa dữ liệu xếp hạng
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Xử lý phản hồi từ server
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] playerDataList = response.split("\\|"); // Giả sử server trả về các bản ghi cách nhau bởi '|'

            // Duyệt qua từng bản ghi xếp hạng và tạo đối tượng Player
            for (String record : playerDataList) {
                String[] playerData = record.split("&");
                if (playerData.length == 9) {  // Kiểm tra nếu dữ liệu đầy đủ
                    String playerID = playerData[0].split("=")[1];
                    String username = playerData[1].split("=")[1];
                    int totalGames = Integer.parseInt(playerData[2].split("=")[1]);
                    int totalWins = Integer.parseInt(playerData[3].split("=")[1]);
                    int totalScore = Integer.parseInt(playerData[4].split("=")[1]);
                    int averageScore = Integer.parseInt(playerData[5].split("=")[1]);
                    int status = Integer.parseInt(playerData[6].split("=")[1]);
                    int isPlaying = Integer.parseInt(playerData[7].split("=")[1]);

                    // Chuyển đổi chuỗi thành Timestamp
                    String timestampStr = playerData[8].split("=")[1];

                    // Kiểm tra và bổ sung phần thời gian nếu thiếu
                    if (timestampStr.length() == 10) {  // Nếu chỉ có yyyy-MM-dd
                        timestampStr += " 00:00:00";    // Thêm thời gian mặc định
                    }

                    try {
                        Timestamp createdAt = Timestamp.valueOf(timestampStr);  // Parse string to Timestamp
                        topPlayers.add(new Player(playerID, username, totalGames, totalWins, totalScore, averageScore, status, isPlaying, createdAt));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid timestamp format: " + timestampStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topPlayers;
    }
}

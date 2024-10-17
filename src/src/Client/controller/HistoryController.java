package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Client.model.PlayerGame;

public class HistoryController {
    private static final String SERVER_ADDRESS = "26.29.9.209"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;

    public List<PlayerGame> getPlayerHistory(String playerID) {
        List<PlayerGame> historyList = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=history&playerID=playerID"
            String message = "type=history&playerID=" + playerID;
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[4096]; // Kích thước buffer lớn hơn để chứa nhiều dữ liệu
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Xử lý phản hồi từ server
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] historyData = response.split("\\|"); // Giả sử server trả về dữ liệu cách nhau bằng '|'

            // Duyệt qua từng bản ghi lịch sử và tạo đối tượng PlayerGame
            for (String record : historyData) {
                String[] playerGameData = record.split("&");
                if (playerGameData.length == 7) {  // Kiểm tra dữ liệu đầy đủ
                    String gameId = playerGameData[0].split("=")[1];
                    Timestamp joinTime = Timestamp.valueOf(playerGameData[1].split("=")[1]); // Chuyển đổi chuỗi thành Timestamp
                    Timestamp leaveTime = Timestamp.valueOf(playerGameData[2].split("=")[1]);
                    Integer playDuration = Integer.parseInt(playerGameData[3].split("=")[1]);
                    int score = Integer.parseInt(playerGameData[4].split("=")[1]);
                    String result = playerGameData[5].split("=")[1]; // 'win', 'lose', 'draw'
                    Boolean isFinal = Boolean.parseBoolean(playerGameData[6].split("=")[1]);

                    historyList.add(new PlayerGame(playerID, gameId, joinTime, leaveTime, playDuration, score, result, isFinal));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyList;
    }
}


package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LoginController {
    private static final String SERVER_ADDRESS = "localhost"; // Thay đổi nếu server chạy trên một máy khác
    private static final int SERVER_PORT = 12345;

    public boolean authenticate(String username, String password) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=login&username=user&password=pass"
            String message = "type=login&username=" + username + "&password=" + password;
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Xử lý phản hồi
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            return response.equals("login success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

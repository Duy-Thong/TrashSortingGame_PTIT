package Client.controller;

import Client.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RegisterController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT; // Cổng server

    public boolean register(String username, String password) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp đăng ký
            String message = "type=register&username=" + username + "&password=" + password;
            System.out.println(message);
            byte[] buffer = message.getBytes();

            // Gửi thông điệp đến server
            InetAddress address = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            return response.equals("registration_success");
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }
}
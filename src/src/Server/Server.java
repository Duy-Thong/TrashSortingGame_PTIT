package Server;

import Client.DTO.ClientInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientInfo> clients = new ArrayList<>(); // Lưu danh sách client
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Lưu thông tin client nếu chưa có
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                addClient(clientAddress, clientPort);

                String message = new String(packet.getData(), 0, packet.getLength());
                String[] parts = message.split("&");
                String type = parts[0].split("=")[1];

                String response = "";

                if (type.equals("login")) {
                    String username = parts[1].split("=")[1];
                    String password = parts[2].split("=")[1];
                    boolean isAuthenticated = authenticate(username, password);
                    response = isAuthenticated ? "login success" : "login failure";
                }
                if (type.equals("UPDATE_SCORE")) {
//                    response = isAuthenticated ? "login success" : "login failure";
                    System.out.println("da nhan");
                    sendToAllClients(socket, "hehehe", clientAddress, clientPort);
                }

                if(response.length() != 0 ) {
                    // Phản hồi lại client
                    byte[] responseBuffer = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm xác thực tài khoản
    private static boolean authenticate(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/trashsortinggame";
        String dbUser = "root"; // Thay bằng tên đăng nhập database của bạn
        String dbPassword = ""; // Thay bằng mật khẩu database của bạn

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm thêm client vào danh sách nếu chưa có
    private static void addClient(InetAddress address, int port) {
        for (ClientInfo client : clients) {
            if (client.getAddress().equals(address) && client.getPort() == port) {
                return; // Client đã có trong danh sách
            }
        }
        clients.add(new ClientInfo(address, port));
    }

    // Gửi tin nhắn đến tất cả các client ngoại trừ client đã gửi
    private static void sendToAllClients(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort) throws Exception {
        byte[] data = message.getBytes();
        for (ClientInfo client : clients) {
//            if (!client.getAddress().equals(senderAddress) || client.getPort() != senderPort) {
                DatagramPacket packet = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
                socket.send(packet);
//            }
        }
    }
}

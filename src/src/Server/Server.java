package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                String[] parts = message.split("&");
                String type = parts[0].split("=")[1];
                String username = parts[1].split("=")[1];
                String password = parts[2].split("=")[1];

                String response = "";

                if (type.equals("login")) {
                    boolean isAuthenticated = authenticate(username, password);
                    response = isAuthenticated ? "login success" : "login failure";
                }

                // Phản hồi lại client
                byte[] responseBuffer = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
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
}

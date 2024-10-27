package Client.controller;
import Client.model.Account;
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
            System.out.println("Sending: " + message);
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
            if (response.equals("login success")) {
                new Thread(new InviteController.InviteListener()).start();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getAccountIDByUsername(String username) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=getAccountID&username=user"
            String message = "type=getAccountID&username=" + username;
            System.out.println("Sending: " + message);
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPlayerIDByAccountID(String accountID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=getPlayerID&accountID=uuid"
            String message = "type=getPlayerID&accountID=" + accountID;
            System.out.println("Sending: " + message);
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Account getAccountByAccountID(String accountID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=getAccount&accountID=uuid"
            String message = "type=getAccount&accountID=" + accountID;
            System.out.println("Sending: " + message);
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

            // Xử lý phản hồi
            String[] parts = response.split("&");
            String username = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            String role = parts[2].split("=")[1];
            return new Account(accountID, username, password,role);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void logout(String playerID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=logout&playerID=playerID"
            String message = "type=logout&playerID=" + playerID;
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
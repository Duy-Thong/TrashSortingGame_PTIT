package Client.controller.admin;

import Client.controller.LoginController;
import Client.model.Account;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementController {
    private static final String SERVER_ADDRESS = "26.79.24.79"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;
    private static final LoginController loginController = new LoginController();
    // Hàm lấy danh sách tất cả các account từ cơ sở dữ liệu
    public static List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp gửi đến server, ví dụ: "type=getAllAccount"
            String message = "type=getAllAccount";
            byte[] buffer = message.getBytes();

            // Gửi gói tin đến server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] responseBuffer = new byte[2048]; // Kích thước buffer để chứa dữ liệu account
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Xử lý phản hồi từ server
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] accountDataList = response.split("\\|"); // Giả sử server trả về các bản ghi cách nhau bởi '|'

            // Duyệt qua từng bản ghi account và tạo đối tượng Account
            for (String record : accountDataList) {
                String[] accountData = record.split("&");
                if (accountData.length == 4) {  // Kiểm tra nếu dữ liệu đầy đủ
                    String accountID = accountData[0].split("=")[1];
                    String username = accountData[1].split("=")[1];
                    String password = accountData[2].split("=")[1];
                    String role =accountData[3].split("=")[1];

                    accountList.add(new Account(accountID, username, password, role));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accountList;
    }

    public static void deleteAccount(String accountID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo gọi tin định nghĩa này
            String message = "type=deleteAccount&accountID=" + accountID;
            byte[] buffer = message.getBytes();

            // Gửi gói tin định nghĩa được server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Account getAccountByID(String accountID) {
        Account account = loginController.getAccountByAccountID(accountID);
        return account;
    }
    public static void updateAccount(String accountID, String username, String password, String role) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo gọi tin định nghĩa này
            String message = "type=updateAccount&accountID=" + accountID + "&username=" + username + "&password=" + password + "&role=" + role;
            byte[] buffer = message.getBytes();
            // Gửi gói tin định nghĩa được server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addAccount(String username, String password, String role) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo gọi tin định nghĩa này
            String message = "type=addAccount&username=" + username + "&password=" + password + "&role=" + role;
            byte[] buffer = message.getBytes();
            // Gửi gói tin định nghĩa không server
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

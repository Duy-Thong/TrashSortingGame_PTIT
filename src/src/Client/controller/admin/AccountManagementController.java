package Client.controller.admin;

import Client.model.Account;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementController {
    private static final String SERVER_ADDRESS = "26.79.24.79"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;

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

}

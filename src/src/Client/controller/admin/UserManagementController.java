package Client.controller.admin;

import Client.model.Account;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UserManagementController {
    private static final String SERVER_ADDRESS = "localhost"; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = 12345;

    public List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=getAllAccount"
            String message = "type=getAllAccount";
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
            String[] accountDataArray = response.split("\\|"); // Giả sử server trả về dữ liệu cách nhau bằng '|'

            // Duyệt qua từng bản ghi account và tạo đối tượng Account
            for (String record : accountDataArray) {
                String[] accountDataParts = record.split("&");
                if (accountDataParts.length == 3) {  // Kiểm tra dữ liệu đầy đủ
                    String userID = accountDataParts[0].split("=")[1];
                    String username = accountDataParts[1].split("=")[1];
                    String role = accountDataParts[2].split("=")[1];

                    accountList.add(new Account(userID, username, "", role)); // Assuming password is not sent in this response
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }
}

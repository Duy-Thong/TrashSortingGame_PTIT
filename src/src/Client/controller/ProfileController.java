package Client.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ProfileController {

    private static final int SERVER_PORT = 12345;
    private static final String SERVER_ADDRESS = "localhost";

    public String[] getPlayerProfile(String playerID) {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Prepare request to send to the server
            String request = "type=profile&playerID=" + playerID;
            byte[] buffer = request.getBytes();
            InetAddress serverAddr = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddr, SERVER_PORT);

            // Send request to the server
            socket.send(packet);

            // Prepare buffer for receiving the response
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Parse server response
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength()).trim();
            if (response.startsWith("error")) {
                return null;
            }

            // Split response to get player data (username, totalGames, totalWins, totalScore, avgScore, createdAt)
            return response.split("&");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

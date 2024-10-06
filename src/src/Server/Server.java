package Server;

import Server.model.PlayHistoryDTO;
import Server.model.RankPlayerDTO;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class Server {
    private static final int PORT = 12346;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("Server is running on port " + PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                System.out.println("Waiting for packet...");
                socket.receive(packet);
                System.out.println("Packet received!");
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                handleClientRequest(message, packet, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(String message, DatagramPacket packet, DatagramSocket socket) {
        if (message == null || message.isEmpty()) {
            System.out.println("Received an empty or null message.");
            return;
        }

        String[] parts = message.split("&");
        String type = parts[0].split("=")[1];

        String response;
        if (type.equals("login")) {
            String username = parts[1].split("=")[1];
            String password = parts[2].split("=")[1];
            boolean isAuthenticated = DatabaseHelper.authenticate(username, password);
            response = isAuthenticated ? "login success" : "login failure";
        } else if (type.equals("register")) {
            String username = parts[1].split("=")[1];
            String password = parts[2].split("=")[1];
            boolean isRegistered = DatabaseHelper.register(username, password);
            response = isRegistered ? "registration_success" : "registration_failure";
        } if (type.equals("getRank")) {
            response ="";
            List<RankPlayerDTO> rankPlayerDTOS = DatabaseHelper.getRankPlayer();
            if(rankPlayerDTOS.size() !=0){
                for(RankPlayerDTO rankPlayerDTO : rankPlayerDTOS){
                    response += rankPlayerDTO.toString() + " || ";
                }
            }
            else response = "No record player";

        } if (type.equals("getHistory")) {
            String playerID = parts[1].split("=")[1];
            response ="";
            List<PlayHistoryDTO> playHistoryDTOS = DatabaseHelper.getPlayHistory(playerID);
            if(playHistoryDTOS.size() !=0){
                for(PlayHistoryDTO playHistoryDTO : playHistoryDTOS){
                    response += playHistoryDTO.toString() + " || ";
                }
            }
            else response = "No record player";

        } else {
            response = "unknown_request";
        }

        System.out.println("Sending response: " + response);

        byte[] responseBuffer = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

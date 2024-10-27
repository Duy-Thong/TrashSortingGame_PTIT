package Client.controller;

import Client.Constants;
import Client.model.Bin;
import Client.model.TrashItem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class HelpController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT;

    public HelpController() {
    }

    public List<TrashItem> fetchTrashItemData() {
        List<TrashItem> trashItems = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=getTrashItemData";
            byte[] buffer = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            byte[] responseBuffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] items = response.split("\\|"); // Split using pipe symbol
            for (String item : items) {
                String[] parts = item.split(";"); // Split using semicolon
                if (parts.length == 4) { // Ensure there are exactly 4 parts
                    String id = parts[0];
                    String name = parts[1];
                    String type = parts[2];
                    String url = parts[3];
                    trashItems.add(new TrashItem(id, name, type, url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trashItems;
    }

    public List<Bin> fetchBinData() {
        List<Bin> bins = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=getBinData";
            byte[] buffer = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            byte[] responseBuffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            String[] items = response.split("\\|"); // Split using pipe symbol
            for (String item : items) {
                String[] parts = item.split(";"); // Split using semicolon
                if (parts.length == 4) { // Ensure there are exactly 4 parts
                    String id = parts[0];
                    String name = parts[1];
                    String type = parts[2];
                    String url = parts[3];
                    bins.add(new Bin(id, name, type, url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bins;
    }

}
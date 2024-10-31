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
                if (parts.length == 5) { // Ensure there are exactly 4 parts
                    String id = parts[0];
                    String name = parts[1];
                    String type = parts[2];
                    String url = parts[3];
                    String description = parts[4];
                    trashItems.add(new TrashItem(id, name, type, url, description));
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
                if (parts.length == 5) { // Ensure there are exactly 4 parts
                    String id = parts[0];
                    String name = parts[1];
                    String type = parts[2];
                    String url = parts[3];
                    String description = parts[4];
                    bins.add(new Bin(id, name, type, url, description));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bins;
    }

    public void addTrashItem(TrashItem newItem) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=addTrashItem&name=" + newItem.getName() + "&type=" + newItem.getType() + "&url=" + newItem.getUrl();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBin(Bin newBin) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=addBin&name=" + newBin.getName() + "&type=" + newBin.getType() + "&url=" + newBin.getUrl();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTrashItem(TrashItem updatedItem) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=updateTrashItem&id=" + updatedItem.getId() + "&name=" + updatedItem.getName() + "&type=" + updatedItem.getType() + "&url=" + updatedItem.getUrl();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBin(Bin updatedBin) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=updateBin&id=" + updatedBin.getId() + "&name=" + updatedBin.getName() + "&type=" + updatedBin.getType() + "&url=" + updatedBin.getUrl();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteTrashItem(String type) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=deleteTrashItem&type=" + type;
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteBin(String type) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=deleteBin&type=" + type;
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
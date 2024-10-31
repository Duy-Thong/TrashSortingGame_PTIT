package Client.controller.admin;

import Client.Constants;
import Client.model.Bin;
import Client.model.TrashItem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemManagementController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT;

    public ItemManagementController() {
    }
    public List<String> getTrashTypes() {
        List<String> types = new ArrayList<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=getTrashTypes";
            byte[] buffer = message.getBytes();

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);

            byte[] responseBuffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            types = Arrays.asList(response.split(";"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
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

    public void addTrashItem(TrashItem trashItem) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=addTrashItem&id=" + trashItem.getId() + "&name=" + trashItem.getName() + "&kind=" + trashItem.getType() + "&url=" + trashItem.getUrl() + "&description=" + trashItem.getDescription();
            System.out.println(message);
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBin(Bin bin) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=addBin&id=" + bin.getId() + "&name=" + bin.getName() + "&kind=" + bin.getType() + "&url=" + bin.getUrl() + "&description=" + bin.getDescription();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTrashItem(TrashItem trashItem) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=updateTrashItem&id=" + trashItem.getId() + "&name=" + trashItem.getName() + "&kind=" + trashItem.getType() + "&url=" + trashItem.getUrl() + "&description=" + trashItem.getDescription();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBin(Bin bin) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "type=updateBin&id=" + bin.getId() + "&name=" + bin.getName() + "&kind=" + bin.getType() + "&url=" + bin.getUrl() + "&description=" + bin.getDescription();
            byte[] buffer = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package Client.controller;

import Client.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DataController {
    private static final String SERVER_ADDRESS = Constants.IP_SERVER; // Thay đổi nếu server chạy trên máy khác
    private static final int SERVER_PORT = Constants.PORT;

    public static List<String> getListTrash(){
        String[] trashitems = getData("getTrashs");
        List<String> urls = getUrls(trashitems);
        return urls;
    }

    public static List<String> getListTypesTrash(){
        String[] trashitems = getData("getTrashs");
        List<String> types = getTypes(trashitems);
        return types;
    }

    public static List<String> getListNameTrash(){
        String[] trashitems = getData("getTrashs");
        List<String> names = getNames(trashitems);
        return names;
    }

    public static List<String> getListBin(){
        String[] trashbins = getData("getBins");
        List<String> urls = getUrls(trashbins);
        return urls;
    }

    public static List<String> getListTypesBin(){
        String[] trashbins = getData("getBins");
        List<String> types = getTypes(trashbins);
        return types;
    }

    public static List<String> getListNameBin(){
        String[] trashbins = getData("getBins");
        List<String> names = getNames(trashbins);
        return names;
    }

    private static String[] getData(String type){
        try (DatagramSocket socket = new DatagramSocket()) {
            // Tạo thông điệp dạng: "type=history&playerID=playerID"
            String message = "type=" + type + "&";
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
            String[] recieved = response.split("\\|"); // Giả sử server trả về dữ liệu cách nhau bằng '|'
            return  recieved;
        } catch (Exception e) {
            System.out.println("errot");
            e.printStackTrace();
            return null;
        }
    }
    private static List<String> getUrls(String[] ls){
        List<String> urls = new ArrayList<>();
        for(int i = 0; i < ls.length; i++){
            String[] tmp = ls[i].split("&");
            String url = tmp[2].split("=")[1];
            urls.add(url);
        }
        return urls;
    }

    private static List<String> getNames(String[] ls){
        List<String> names = new ArrayList<>();
        for(int i = 0; i < ls.length; i++){
            String[] tmp = ls[i].split("&");
            String name = tmp[0].split("=")[1];
            names.add(name);
        }
        return names;
    }

    private static List<String> getTypes(String[] ls){
        List<String> types = new ArrayList<>();
        for(int i = 0; i < ls.length; i++){
            String[] tmp = ls[i].split("&");
            String type = tmp[1].split("=")[1];
            types.add(type);
        }
        return types;
    }

}
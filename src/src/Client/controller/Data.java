package Client.controller;

import java.util.Arrays;
import java.util.List;

public class Data {
    public static List<String> getListTrash(){
        List<String> urls = Arrays.asList(
                "https://cdn0.iconfinder.com/data/icons/ecology-197/100/Ecology_Water_Pollution-64.png",
                "https://cdn1.iconfinder.com/data/icons/garbage-9/1000/Garbage-09-64.png",
                "https://cdn3.iconfinder.com/data/icons/save-the-world/64/BOTTLE_RECYCLE-64.png"
        );
        return urls;
    }

    public static List<String> getListBin(){
        List<String> urls = Arrays.asList(
                "https://cdn3.iconfinder.com/data/icons/font-awesome-regular-1/512/trash-can-64.png",
                "https://cdn1.iconfinder.com/data/icons/unicons-line-vol-6/24/trash-alt-64.png",
                "https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/trash-64.png",
                "https://cdn2.iconfinder.com/data/icons/ios-7-icons/50/trash-64.png"
        );
        return urls;
    }

    public static List<String> getListTypes(){
        List<String> urls = Arrays.asList(
                "paper",
                "plastic",
                "metal",
                "bottel"
        );
        return urls;
    }

}
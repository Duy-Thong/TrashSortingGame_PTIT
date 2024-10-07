package Client.model;

public class TrashItem {
    private String itemID;
    private String name;
    private String type; // 'organic', 'plastic', 'metal', 'paper'
    private String imgUrl;

    public TrashItem() {}

    public TrashItem(String itemID, String name, String type, String imgUrl) {
        this.itemID = itemID;
        this.name = name;
        this.type = type;
        this.imgUrl = imgUrl;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}


package Client.model;

public class TrashBin {
    private String binID;
    private String name;
    private String type; // 'organic', 'plastic', 'metal', 'paper'
    private String imgUrl;

    public TrashBin() {}

    public TrashBin(String binID, String name, String type, String imgUrl) {
        this.binID = binID;
        this.name = name;
        this.type = type;
        this.imgUrl = imgUrl;
    }

    public String getBinID() {
        return binID;
    }

    public void setBinID(String binID) {
        this.binID = binID;
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


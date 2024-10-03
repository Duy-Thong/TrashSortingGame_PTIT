package model.wasteItems;

public class Magazine extends WasteItem {

    private static final int code = 6;
    public static final String belongsTo = "Paper Bin";
    private static final String IMAGE_PATH = "src/data/images/waste_items/magazine.png";
    public Magazine(int x) {
        super(x);
        belongedBin = belongsTo;
        codeNum = code;
        this.name = "Magazine";
        imagePath = IMAGE_PATH;
    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }
}

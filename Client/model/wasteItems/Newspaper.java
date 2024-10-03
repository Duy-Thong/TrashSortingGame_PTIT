package model.wasteItems;

public class Newspaper extends WasteItem {
    private static final String IMAGE_PATH = "src/data/images/waste_items/newspaper.png";
    public static final String belongsTo = "Paper Bin";
    private static final int code = 7;

    public Newspaper(int x) {
        super(x);
        belongedBin = belongsTo;
        codeNum = code;
        this.name = "Newspaper";
        imagePath = IMAGE_PATH;
    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }
}

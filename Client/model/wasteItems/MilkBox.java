package model.wasteItems;

public class MilkBox extends WasteItem {

    private static final String IMAGE_PATH = "src/data/images/waste_items/milk-box.png";
    public static final String belongsTo = "Recyclable Containers Bin";
    private static final int code = 5;

    public MilkBox(int x) {
        super(x);
        belongedBin = belongsTo;
        codeNum = code;
        this.name = "Milk box";
        imagePath = IMAGE_PATH;
    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }
}

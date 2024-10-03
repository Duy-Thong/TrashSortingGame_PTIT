package model.wasteItems;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlasticBag extends WasteItem {
    private static final int code = 4;
    public static final String belongsTo = "Garbage Bin";

    //Icons made by <a href="https://www.flaticon.com/authors/good-ware"
    // title="Good Ware">Good Ware</a> from
    // <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
    private static final String IMAGE_PATH = "src/data/images/waste_items/plastic_bag.png";

    public PlasticBag(int x) {
        super(x);
        belongedBin = belongsTo;
        codeNum = code;
        this.name = "Plastic Bag";
        imagePath = IMAGE_PATH;
        try {
            i = ImageIO.read(new File(imagePath)).getScaledInstance(SIZE_X,SIZE_Y, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("Fail to read image of items");
            e.printStackTrace();
        }

    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }

}

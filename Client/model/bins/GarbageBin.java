package model.bins;

import java.awt.*;

public class GarbageBin extends Bin {

    private static final Color COLOR = new Color(23, 23, 23);
    private static final String IMAGE_PATH = "src/data/images/bins/garbage_bin.png";

    public GarbageBin() {
        super("Garbage Bin",700);
        this.color = COLOR;
        imagePath = IMAGE_PATH;
//        {
//            try {
//                i = ImageIO.read(new File(imagePath)).getScaledInstance(SIZE_X,SIZE_Y,Image.SCALE_DEFAULT);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.print("fail to read image of bins");
//            }
//        }
    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }
}

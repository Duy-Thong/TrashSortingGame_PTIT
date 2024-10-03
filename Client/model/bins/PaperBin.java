package model.bins;

import java.awt.*;

public class PaperBin extends Bin {

    private static final Color COLOR = new Color(46, 81, 175);
    private static final String IMAGE_PATH = "src/data/images/bins/paper_bin_blue.png";
    public PaperBin() {
        super("Paper Bin",500);
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

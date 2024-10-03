package model.bins;

import java.awt.*;

public class FoodScrapBin extends Bin {

    private static final Color COLOR = new Color(16, 188, 83);
    private static final String IMAGE_PATH = "src/data/images/bins/food_scrap_bin_green.png";
    public FoodScrapBin() {
        super("Food Scrap \nBin",100);
        color = COLOR;
//        imagePath = IMAGE_PATH;
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


//    @Override
//    public void render(Graphics g) {
//        Color savedCol = g.getColor();
//        g.setColor(COLOR);
//        g.fillRect(getX() - SIZE_X / 2, getY() - SIZE_Y / 2, SIZE_X, SIZE_Y);
//        g.setColor(savedCol);
//    }
}

package model.wasteItems;

public class GlassBottle extends WasteItem {
    private static final int code = 3;
    public static final String belongsTo = "Recyclable Containers Bin";

    // Icons made by <a href="https://www.flaticon.com/authors/freepik"
    // title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/"
    // title="Flaticon"> www.flaticon.com</a>
    private static final String IMAGE_PATH = "src/data/images/waste_items/thin-bottle-of-water.png";

    public GlassBottle(int x) {
        super(x);
        belongedBin = belongsTo;
        codeNum = code;
        this.name = "Glass bottle";
        imagePath = IMAGE_PATH;
    }

    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }

//    @Override
//    public void render(Graphics g) throws IOException {
//        Color savedCol = g.getColor();
//        g.setColor(Color.BLACK);
//        Image i = ImageIO.read(new File(imagePath)).getScaledInstance(50,60,Image.SCALE_DEFAULT);
//        g.drawImage(i,x,y,null);
//        g.setColor(Color.WHITE);
//        g.drawString(name,x,y + SIZE_Y);
//        g.setColor(savedCol);
//    }

}

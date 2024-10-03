package model.bins;

import java.awt.*;

public class RecyclableContainersBin extends Bin {
    private static final Color COLOR = new Color(184, 184, 184);
    private static final String IMAGE_PATH = "src/data/images/bins/container_bin_grey.png";

    public RecyclableContainersBin() {
        super("Recyclable \nContainers Bin",300);
        this.color = COLOR;
        imagePath = IMAGE_PATH;
    }


    @Override
    protected String getPath() {
        return IMAGE_PATH;
    }
}

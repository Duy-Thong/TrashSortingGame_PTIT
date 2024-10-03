package model.bins;

import model.WSGame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public abstract class Bin {

    //width & height
    protected static final int SIZE_X = 100;
    protected static final int SIZE_Y = 90;
    //coordinates
    protected int x;
    protected int y;
    protected Color color;
    protected String imagePath;

    protected String name;
    protected Image i;
    private int lineHeight;



    public Bin(String name, int x) {
        this.name = name;
        this.x = x;
        this.y = WSGame.HEIGHT- SIZE_Y / 2;
        {
            try {
                i = ImageIO.read(new File(getPath())).getScaledInstance(SIZE_X,SIZE_Y,Image.SCALE_DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.print("fail to read image of bins");
            }
        }
    }

    protected abstract String getPath();

//    public String getName() {
//        return name;
//    }
//
//    public int getSizeX() { return SIZE_X; }

    public static int getSizeY() { return SIZE_Y; }
//
//    public int getX() { return x;}
//
//    public int getY() { return y;}

    public void render(Graphics g){
        Color savedCol = g.getColor();
        g.setColor(color);
        lineHeight = g.getFontMetrics().getHeight();
        drawString(g,name,x- SIZE_X / 2,y);
        g.drawImage(i,x- SIZE_X / 2, y - SIZE_Y ,null);
        // instead to g.drawString, call method drawString to split the lines
        g.setColor(savedCol);
    }

    private void drawString(Graphics g, String str, int x, int y) {
        g.setColor(color.WHITE);
        g.setFont(new Font("Arial Black", 1,14));
        for (String line : name.split("\n"))
            g.drawString(line, x, y += lineHeight);
    }
}

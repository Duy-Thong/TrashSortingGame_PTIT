/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author vutuyen
 */
public class Bin {
    private String type;
    private int x, y;
    private String url;
    private ImageIcon binImage;
    private int widthImage = 64, heightImage = 64;

    public Bin(int x, int y, String type, String url) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.url = url;
        this.binImage = urlToImage(url);
    }

    private ImageIcon urlToImage(String urlString){
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            return imageIcon;
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return new ImageIcon();
        }
    }

    public void draw(Graphics g) {
        Image img = binImage.getImage();
        if (img != null) {
            int img_width = img.getWidth(null);
            int img_height = img.getHeight(null);
            g.drawImage(img, x - img_width/2, y - img_height/2, img_width, img_height, null);
        }
    }

    public Bin copy(){
        return new Bin(
                this.x,
                this.y,
                this.type,
                this.url
        );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidthImage() {
        return widthImage;
    }

    public void setWidthImage(int widthImage) {
        this.widthImage = widthImage;
    }

    public int getHeightImage() {
        return heightImage;
    }

    public void setHeightImage(int heightImage) {
        this.heightImage = heightImage;
    }
}
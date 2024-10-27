/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;


public class TrashItem{
    private int x, y = 0;
    private int step = 3;
    private int index;
    private String id;
    private String name;
    private String type;
    private String url;
    private int widthImage = 40, heightImage = 40;
    private ImageIcon trashImages;

    public TrashItem(int x, int y, int index, String type, String url) {
        this.x = x;
        this.y = y;
        this.index = index;
        this.type = type;
        this.url = url;
        this.trashImages = urlToImage(this.url);
    }
    public TrashItem(String id, String name, String type, String url) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
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
        Image img = trashImages.getImage();
        if (img != null) {
            int img_width = img.getWidth(null);
            int img_height = img.getHeight(null);
            g.drawImage(img, x - img_width/2, y - img_height/2, img_width, img_height, null);
        }
    }

    public void move() {
        this.y += step;
    }

    public TrashItem copy(){
        return new TrashItem(
                this.x,
                this.y,
                this.index,
                this.type,
                this.url
        );
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y){
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getHeightImage() {
        return heightImage;
    }

    public void setHeightImage(int heightImage) {
        this.heightImage = heightImage;
    }

    public int getWidthImage() {
        return widthImage;
    }

    public void setWidthImage(int widthImage) {
        this.widthImage = widthImage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
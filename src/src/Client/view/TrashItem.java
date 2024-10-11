/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client.view;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

/**
 *
 * @author vutuyen
 */
public class TrashItem{
    private int x, y, step = 10;
    private String type;
    public static final int SIZE = 40;
    
    public TrashItem(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void move() {
        y += step;
    }
    
    public void draw(Graphics g, HashMap<String, Image> trashImages) {
        Image img = trashImages.get(type);
        if (img != null) {
            g.drawImage(img, x, y, SIZE, SIZE, null);
        }
    }
    public int getY() {
        return y;
    }
    
    public int getX(){
        return x;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public String getType() {
        return type;
    }
      
}

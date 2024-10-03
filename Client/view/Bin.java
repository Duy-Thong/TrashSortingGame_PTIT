/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author vutuyen
 */
public class Bin{
    public static JLabel createBinLabel(String color) {
        ImageIcon icon = new ImageIcon("/home/vutuyen/NetBeansProjects/JavaApplication8/src/images/trash2.png");
        JLabel label = new JLabel(icon);
        label.setToolTipText(color + " Bin");
        return label;
    }
    
    public static JLabel addEvent(){
        JLabel a = new JLabel("text");
        
        return a;
    }
}

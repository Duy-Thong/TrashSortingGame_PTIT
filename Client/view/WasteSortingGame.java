package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author vutuyen
 */

public class WasteSortingGame extends JFrame {
    
    private RunGame game;
    public WasteSortingGame() {
        setTitle("Waste Sorting Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupStartGame();
    }
    
    // Open Game
    private void setupStartGame() {
        JPanel ok = new JPanel(new GridLayout(1, 1));
        JButton button = new JButton("OK");
        button.addActionListener((ActionEvent e) -> {
          game = new RunGame();
          this.setVisible(false);
          game.setVisible(true);
        });
        ok.add(button);
        add(ok, BorderLayout.CENTER);
    }
    
    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WasteSortingGame home = new WasteSortingGame();
            home.setVisible(true);
        });
    }
}
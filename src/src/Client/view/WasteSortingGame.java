package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Client.controller.DataController;

/**
 *
 * @author vutuyen
 */

public class WasteSortingGame extends JFrame {

    private RunGame game;
    public WasteSortingGame() {
        setTitle("Waste Sorting Game");
        setSize(810, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupStartGame();
        System.out.println(DataController.getListTrash());
        System.out.println(DataController.getListBin());
        setVisible(true);
    }

    // Open Game
    private void setupStartGame() {
        JPanel ok = new JPanel(new GridLayout(1, 1));
        JButton button = new JButton("OK");
        button.addActionListener((ActionEvent e) -> {
            game = new RunGame("roomId","idPlayer", "idplayer2");
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
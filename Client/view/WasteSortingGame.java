package ui;

import model.WSGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WasteSortingGame extends JFrame {
    private WSGame game;
    private GamePanel gamePanel;
    private static final int INTERVAL = 5;
    private Timer timer;
    private ScorePanel scorePanel;

    // Constructs main window
    // effects: sets up window in which Space Invaders game will be played
    public  WasteSortingGame() {
        super("Waste Sorting Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new WSGame();
        gamePanel = new GamePanel(game);
        scorePanel = new ScorePanel(game);
        add(gamePanel);
        add(scorePanel, BorderLayout.NORTH);
        pack();
        addKeyListener(new KeyHandler());
        centreOnScreen();
        setVisible(true);
        addTimer();
        timer.start();
        game.addObserver(scorePanel);
}

    // switch to the given panel

    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scrn.width - getWidth()) / 2, (scrn.height - getHeight()) / 2);
    }

    /*
     * A key handler to respond to key events
     */
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            game.keyPressed(e.getKeyCode());
        }
    }

    // Set up timer
    // modifies: none
    // effects:  initializes a timer that updates game each
    //           INTERVAL milliseconds
    private void addTimer() {
        timer = new Timer(INTERVAL, ae -> {
            game.update(this);
            gamePanel.repaint();
        });
    }
    public void stopTimer() {
        timer.stop();
    }

    public static void main(String[] args) {
        new InstructionFrame();
    }
}

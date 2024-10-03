package ui;

import model.WSGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// The game is rendered here.

public class GamePanel extends JPanel {
    private static final String OVER = "Game Over!";
    private static final String REPLAY = "R to replay";

    //<a href="https://www.freepik.com/free-photos-vectors/tree">
    // Tree vector created by upklyak - www.freepik.com</a>
    private static final String imagePath = "src/data/images/background.jpg";
    private WSGame game;
    private Image bgImg;

    // Constructs a game panel
    // effects:  sets size and background colour of panel,
    //           updates this with the game to be displayed
    public GamePanel(WSGame g) {
        setPreferredSize(new Dimension(WSGame.WIDTH, WSGame.HEIGHT));
        this.game = g;
        try {
            bgImg = ImageIO.read(new File(imagePath)).getScaledInstance(WSGame.WIDTH,WSGame.HEIGHT,Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.print("fail to load background image");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bgImg,0,0,null);
        renderGame(g);

        if (game.isOver()){
            gameOver(g);
        }
    }

    // Draws the "game over" message and replay instructions
    // modifies: g
    // effects:  draws "game over" and replay instructions onto g
    private void gameOver(Graphics g) {
        Color saved = g.getColor();
        g.setColor(new Color( 0, 0, 0));
        g.setFont(new Font("Arial", 20, 20));
        FontMetrics fm = g.getFontMetrics();
        centreString(OVER, g, fm, WSGame.HEIGHT / 2);
        centreString(REPLAY, g, fm, WSGame.HEIGHT / 2 + 40);
        g.setColor(saved);
    }

    // Draws the game
    // modifies: g
    // effects:  the game is drawn onto the Graphics object g
    private void renderGame(Graphics g) {
        game.render(g);
    }

    // Centres a string on the screen
    // modifies: g
    // effects:  centres the string str horizontally onto g at vertical position yPos
    private void centreString(String str, Graphics g, FontMetrics fm, int yPos) {
        int width = fm.stringWidth(str);
        g.drawString(str, (WSGame.WIDTH - width) / 2, yPos);
    }
}

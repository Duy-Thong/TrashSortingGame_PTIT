package ui;

import model.WSGame;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class ScorePanel extends JPanel implements Observer {
    private static final String CORRECT_TXT = "Waste correctly sorted: ";
    private static final String INCORRECT_TXT = "Waste misplaced: ";
    private static final int LBL_WIDTH = 200;
    private static final int LBL_HEIGHT = 30;

    private JLabel correctLbl;
    private JLabel misplacedLbl;

    // to construct a score panel
    // effects: set the background color and renders the initial labels;
    //          updates this with the scores
    public ScorePanel(WSGame game) {
        setBackground(new Color(255, 255, 255));
        correctLbl = new JLabel(CORRECT_TXT + 0);
        correctLbl.setPreferredSize(new Dimension(LBL_WIDTH,LBL_HEIGHT));
        misplacedLbl = new JLabel(INCORRECT_TXT + 0);
        misplacedLbl.setPreferredSize(new Dimension(LBL_WIDTH,LBL_HEIGHT));
        add(correctLbl);
        add(Box.createVerticalStrut(10));
        add(misplacedLbl);
    }

    // Updates the score panel
    // modifies: this
    // effects:  updates number of items correctly sorted and number of waste items misplaced
    @Override
    public void update(Observable o, Object arg) {
        if (WSGame.CORRECTLY_SORTED.equals(arg) || WSGame.MISPLACED.equals(arg)) {
            WSGame game = (WSGame) o;
            correctLbl.setText(CORRECT_TXT + game.getCorrectItems());
            misplacedLbl.setText(INCORRECT_TXT + game.getIncorrectItems());
            repaint();
        } else if (WSGame.GameStarts.equals(arg)) {
            WSGame game = (WSGame) o;
            correctLbl.setText(CORRECT_TXT + 0);
            misplacedLbl.setText(INCORRECT_TXT + 0);
            repaint();
        }
    }
}

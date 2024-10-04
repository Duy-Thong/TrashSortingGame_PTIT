package ui;

import model.WSGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class InstructionFrame extends JFrame {

    private JButton startButton;
    Collection<JButton> buttons;

    public static final String gameStarts= "Start the Game!";
    private JPanel instPanel;
    private Dimension dm = new Dimension(300,240);
//    JPanel thisPanel;

    // construct a instruction panel explaining the rules

    public InstructionFrame() {
        super();
        setBackground(new Color(184, 184, 184));
        setSize(dm);
        centreOnScreen();
        setVisible(true);
        startButton = new JButton(gameStarts);
        instPanel = new JPanel();
        instPanel.setLayout(new BoxLayout(instPanel, BoxLayout.Y_AXIS));

        startButton.setActionCommand(gameStarts);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//        thisPanel = this;
        startButton.addActionListener(e -> {
           dispose();
            new WasteSortingGame();
        });

        String instructions = "Please choose from a difficulty level: ";
        String welcome = "Welcome to the Waste Sorting Game!";
        String inst1 = "Use <- or -> key to control the position";
        String inst2 = "Use down arrow or space key to speed up waste falling";

        addALabel(welcome,instPanel);
        addALabel(inst1,instPanel);
        addALabel(inst2,instPanel);
        addALabel(instructions,instPanel);
        createButtons();
        instPanel.add(startButton);
        add(instPanel, BorderLayout.CENTER);
        pack();
    }

    private void createButtons() {
        String[] levels = {"Easy", "Medium", "Difficult"};
        buttons = new ArrayList<>();
        for (String str: levels) {
            JButton btn = new JButton(str);
            buttons.add(btn);
            btn.setActionCommand(str);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> {
                btn.setSelected(true);
                unselectRest(btn);
                WSGame.setDifficultyLevel(e);
            });

            instPanel.add(btn);
        }
    }

    private void addALabel(String text, Container container) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(label);
    }

    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scrn.width - getWidth()) / 2, (scrn.height - getHeight()) / 2);
    }

    private void unselectRest(JButton btn) {
        for (JButton b: buttons) {
            if (!b.equals(btn)) {
                b.setSelected(false);
            }
        }
    }

}

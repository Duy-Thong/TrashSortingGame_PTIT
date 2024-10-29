package Client.view;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author vutuyen
 */
public class EndGame extends JFrame {

    String namePlayer1;
    String namePlayer2;
    int scorePlayer1;
    int scorePlayer2;
    String result;
    public EndGame(RunGame parentGame, String result, String namePlayer1, String namePlayer2, int scorePlayer1, int scorePlayer2) {

        this.namePlayer1 = namePlayer1;
        this.namePlayer2 = namePlayer2;
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.result = result;

        setTitle(result);
        setSize(810, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
//                setBackground(new Color(100, 200, 150)); // Teal-ish background
//                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("WIN!", getWidth()/2 - 50, getHeight()/2 - 50);
            }
        };
        add(panel, BorderLayout.CENTER);

        JLabel scoreLabel = new JLabel("Điểm: " + result, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(scoreLabel, BorderLayout.NORTH);

        JButton restartButton = new JButton("Trở về");
        restartButton.addActionListener(e -> {
//            parentGame.restartGame();
            new LobbyScreen(parentGame.playerId, parentGame.namePlayer1);
            this.dispose();
        });
        add(restartButton, BorderLayout.SOUTH);
    }
}
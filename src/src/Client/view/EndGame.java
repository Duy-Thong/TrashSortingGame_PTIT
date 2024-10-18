package Client.view;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author vutuyen
 */
public class EndGame extends JFrame {
    public EndGame(RunGame parentGame, String winner) {
        setTitle("WIN!");
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

        JLabel scoreLabel = new JLabel("Điểm: " + winner, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(scoreLabel, BorderLayout.NORTH);

        JButton restartButton = new JButton("Trở về");
        restartButton.addActionListener(e -> {
            parentGame.restartGame();
            this.dispose();
        });
        add(restartButton, BorderLayout.SOUTH);
    }
}
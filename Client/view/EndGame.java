package view;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author vutuyen
 */
public class EndGame extends JFrame {
    public EndGame(RunGame parentGame, String winner) {
        setTitle("WIN!");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
//                setBackground(new Color(100, 200, 150)); // Teal-ish background
//                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("WIN!", 150, 100);
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
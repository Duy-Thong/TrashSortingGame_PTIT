package Client.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

/**
 * EndGame JFrame to display the game result, players' names, and scores.
 * Displays the result title in the center with players' names and scores on the sides.
 */
public class EndGame extends JFrame {

    private String namePlayer1;
    private String namePlayer2;
    private int scorePlayer1;
    private int scorePlayer2;
    private String result;

    public EndGame(RunGame parentGame, String result, String namePlayer1, String namePlayer2, int scorePlayer1, int scorePlayer2) {

        this.namePlayer1 = namePlayer1;
        this.namePlayer2 = namePlayer2;
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.result = result;

        setTitle("Game Over - " + result);
        setSize(810, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background panel with custom title in the center
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        // Panel for players' names and scores with padding
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setOpaque(false);

        // Left side for Player 1 with padding
        JLabel player1Label = new JLabel("<html><div style='text-align: center;'>" + namePlayer1 + "<br>Score: " + scorePlayer1 + "</div></html>", SwingConstants.CENTER);
        player1Label.setFont(new Font("Arial", Font.BOLD, 18));
        player1Label.setForeground(Color.WHITE);
        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setOpaque(false);
        player1Panel.setBorder(new EmptyBorder(200, 20, 0, 0)); // Left padding
        player1Panel.add(player1Label, BorderLayout.CENTER);
        scorePanel.add(player1Panel, BorderLayout.WEST);

        // Right side for Player 2 with padding
        JLabel player2Label = new JLabel("<html><div style='text-align: center;'>" + namePlayer2 + "<br>Score: " + scorePlayer2 + "</div></html>", SwingConstants.CENTER);
        player2Label.setFont(new Font("Arial", Font.BOLD, 18));
        player2Label.setForeground(Color.WHITE);
        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Panel.setOpaque(false);
        player2Panel.setBorder(new EmptyBorder(200, 0, 0, 20)); // Right padding
        player2Panel.add(player2Label, BorderLayout.CENTER);
        scorePanel.add(player2Panel, BorderLayout.EAST);

        // Center title between player labels with top margin
        JLabel titleLabel = new JLabel(result.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Top padding of 20 pixels
        scorePanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(scorePanel, BorderLayout.NORTH);

        // Button for returning to the lobby
        JButton restartButton = new JButton("Return to Lobby");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.addActionListener(e -> {
            new LobbyScreen(parentGame.playerId, parentGame.namePlayer1);
            this.dispose();
        });
        add(restartButton, BorderLayout.SOUTH);
    }
}

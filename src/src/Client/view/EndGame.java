package Client.view;

import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class EndGame extends JFrame {
    private final String namePlayer1;
    private final String namePlayer2;
    private final int scorePlayer1;
    private final int scorePlayer2;
    private final String result;
    Font customFont;

    public EndGame(RunGame parentGame, String result, String namePlayer1, String namePlayer2, int scorePlayer1, int scorePlayer2) {
        this.namePlayer1 = namePlayer1;
        this.namePlayer2 = namePlayer2;
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
        this.result = result;

        customFont = loadCustomFont("../assets/FVF.ttf");


        // Create JFrame for the end game screen
        setTitle("Game Over - " + result);
        setSize(810, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load and scale background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("../assets/bgr_endgame.jpg"));
        Image scaledImage = backgroundImage.getImage().getScaledInstance(810, 540, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(scaledImage));
        backgroundLabel.setLayout(new BorderLayout());

        // Main panel for displaying scores and results
        JPanel scorePanel = createScorePanel();
        backgroundLabel.add(scorePanel, BorderLayout.CENTER);

        // Button to return to lobby
        JButton restartButton = createCustomButton("Return to Lobby");
        restartButton.setFont(customFont.deriveFont(Font.BOLD, 12));
        restartButton.addActionListener(e -> {
            new LobbyScreen(parentGame.playerId, parentGame.namePlayer1);
            this.dispose();
        });
        backgroundLabel.add(restartButton, BorderLayout.SOUTH);

        // Result Title
        JLabel titleLabel = new JLabel(result.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 50)); // Increased font size for the title
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add vertical padding to title
        backgroundLabel.add(titleLabel,BorderLayout.NORTH);


        // Add background to JFrame
        add(backgroundLabel);
        setVisible(true);
    }

    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new GridLayout(1, 3)); // Change to 1 row and 3 columns
        scorePanel.setOpaque(false); // Make panel transparent

        // Player 1 Info
        JPanel player1Panel = createPlayerPanel(namePlayer1, scorePlayer1);
        scorePanel.add(player1Panel);

        // Player 2 Info
        JPanel player2Panel = createPlayerPanel(namePlayer2, scorePlayer2);
        scorePanel.add(player2Panel);

        return scorePanel;
    }

    private JPanel createPlayerPanel(String playerName, int playerScore) {
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setOpaque(false); // Make panel transparent

        JLabel playerLabel = new JLabel("<html><div style='text-align: center;'>" + playerName + "<br>Score: " + playerScore + "</div></html>", SwingConstants.CENTER);
        playerLabel.setFont(customFont.deriveFont(Font.BOLD, 18));
        playerLabel.setForeground(Color.BLUE);
        playerPanel.add(playerLabel, BorderLayout.NORTH);

        return playerPanel;
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFont(customFont.deriveFont(Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // Customize button color
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50)); // Adjust button size
        return button;
    }

    // Load custom font method
    private Font loadCustomFont(String fontPath) {
        try (InputStream is = getClass().getResourceAsStream(fontPath)) {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, 18);
        }
    }
}

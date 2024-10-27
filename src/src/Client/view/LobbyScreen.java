package Client.view;

import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class LobbyScreen {
    private final String playerID; // Add playerID field
    private final String username;
    private final LoginController loginController = new LoginController();

    // Constructor accepting playerID
    public LobbyScreen(String playerID, String username) {
        this.playerID = playerID;
        this.username = username;

        // Load custom font
        Font customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to your font file

        // Create JFrame for the lobby screen
        JFrame lobbyFrame = new JFrame("Lobby");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize(810, 540);
        lobbyFrame.setLocationRelativeTo(null);

        // Load GIF background
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/back.gif")); // Path to your GIF file
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout()); // Use GridBagLayout for the background

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make the panel transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Adjusting the spacing between buttons (top, left, bottom, right)

        // Adjusting button positioning and size
        int buttonWidth = 150; // Adjust button width as needed
        int buttonHeight = 40; // Adjust button height as needed

        // Personal Profile Button (Top Left)
        JButton btnProfile = createCustomButton("Trang cá nhân", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnProfile, gbc);

        // History Button (Top Right)
        JButton btnHistory = createCustomButton("Xem lịch sử", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(btnHistory, gbc);

        // Ranking Button (Bottom Left)
        JButton btnRanking = createCustomButton("Xem bảng xếp hạng", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 1; // Moved down a row
        panel.add(btnRanking, gbc);

        // Enter Game Button (Bottom Right)
        JButton btnEnterGame = createCustomButton("Vào game", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 1;
        gbc.gridy = 1; // Moved down a row
        panel.add(btnEnterGame, gbc);

        // Help Button (Above Logout Button)
        JButton btnHelp = createCustomButton("Hướng dẫn", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 2; // Moved down a row
        gbc.gridwidth = 2; // Make the button span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnHelp, gbc);

        // Logout Button (Centered)
        JButton btnLogout = createCustomButton("Đăng xuất", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 3; // Moved down a row
        gbc.gridwidth = 2; // Make the button span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogout, gbc);

        // Add Action Listeners to Buttons
        btnProfile.addActionListener(e -> {
            new ProfileScreen(playerID);
        });

        btnHistory.addActionListener(e -> {
            new HistoryScreen(playerID);
        });

        btnRanking.addActionListener(e -> {
            new RankScreen();
        });

        btnEnterGame.addActionListener(e -> {
            new InviteScreen(playerID, username);
        });

        // Action Listener for Help Button
        btnHelp.addActionListener(e -> {
            new HelpScreen();
        });

        btnLogout.addActionListener(e -> {
            loginController.logout(playerID);
            lobbyFrame.dispose(); // Close lobby screen
            new LoginScreen(); // Open login screen
        });

        // Add panel to background
        backgroundLabel.add(panel);

        // Add background to frame
        lobbyFrame.add(backgroundLabel);
        lobbyFrame.setVisible(true);
    }

    // Method to create a custom button
    private JButton createCustomButton(String text, Font customFont, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(customFont.deriveFont(Font.BOLD, 12)); // Use custom font with smaller size
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // Customize button color
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height)); // Set custom size
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
            return new Font("Serif", Font.PLAIN, 18); // Fallback to default font if loading fails
        }
    }
}

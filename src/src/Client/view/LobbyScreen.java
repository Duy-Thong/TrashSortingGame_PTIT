package Client.view;

import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // Personal Profile Button (Top Left)
        JButton btnProfile = createCustomButton("Trang cá nhân", customFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnProfile, gbc);

        // History Button (Top Right)
        JButton btnHistory = createCustomButton("Xem lịch sử", customFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(btnHistory, gbc);

        // Ranking Button (Bottom Left)
        JButton btnRanking = createCustomButton("Xem bảng xếp hạng", customFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(btnRanking, gbc);

        // Enter Game Button (Bottom Right)
        JButton btnEnterGame = createCustomButton("Vào game", customFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(btnEnterGame, gbc);

        // Logout Button (Centered)
        JButton btnLogout = createCustomButton("Đăng xuất", customFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make the button span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogout, gbc);

        // Add Action Listeners to Buttons
        btnProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Xem Profile cá nhân");
            lobbyFrame.dispose();
            new ProfileScreen(playerID, username);
        });

        btnHistory.addActionListener(e -> {
            JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Xem lịch sử người chơi");
            lobbyFrame.dispose();
            new HistoryScreen(playerID, username);
        });

        btnRanking.addActionListener(e -> {
            JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Bảng xếp hạng");
            lobbyFrame.dispose();
            new RankScreen(playerID, username);
        });

        btnEnterGame.addActionListener(e -> {
            JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Game");
            lobbyFrame.dispose();
            new InviteScreen(playerID, username);
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
    private JButton createCustomButton(String text, Font customFont) {
        JButton button = new JButton(text);
        button.setFont(customFont.deriveFont(Font.BOLD, 12)); // Use custom font with smaller size
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // Customize button color
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50)); // Adjust width to make buttons longer
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

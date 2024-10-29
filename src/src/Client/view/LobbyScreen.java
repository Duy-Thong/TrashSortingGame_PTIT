package Client.view;

import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class LobbyScreen {
    private final String playerID;
    private final String username;
    private final LoginController loginController = new LoginController();

    // Constructor accepting playerID
    public LobbyScreen(String playerID, String username) {
        this.playerID = playerID;
        this.username = username;

        // Load custom font
        Font customFont = loadCustomFont("../assets/FVF.ttf");

        // Create JFrame for the lobby screen
        JFrame lobbyFrame = new JFrame("Eco Guardians");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize(810, 540);
        lobbyFrame.setLocationRelativeTo(null);

        // Load GIF background
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/new_back.gif"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout());

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Spacer to move the buttons down
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0; // Pushes the buttons down
        panel.add(Box.createVerticalStrut(50), gbc); // Adjust the strut size for more spacing

        // Adjusting button positioning and size
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Personal Profile Button (Top Left)
        JButton btnProfile = createCustomButton("Trang cá nhân", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        panel.add(btnProfile, gbc);

        // History Button (Top Right)
        JButton btnHistory = createCustomButton("Xem lịch sử", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(btnHistory, gbc);

        // Ranking Button (Bottom Left)
        JButton btnRanking = createCustomButton("Bảng xếp hạng", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnRanking, gbc);

        // Enter Game Button (Bottom Right)
        JButton btnEnterGame = createCustomButton("Vào game", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnEnterGame, gbc);

        // Help Button (Above Logout Button)
        JButton btnHelp = createCustomButton("Hướng dẫn", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnHelp, gbc);

        // Logout Button (Centered)
        JButton btnLogout = createCustomButton("Đăng xuất", customFont, buttonWidth, buttonHeight);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
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
        btnHelp.addActionListener(e -> {
            new HelpScreen();
        });

        btnLogout.addActionListener(e -> {
            loginController.logout(playerID);
            lobbyFrame.dispose();
            new LoginScreen();
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
        button.setFont(customFont.deriveFont(Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 120));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
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

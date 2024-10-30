package Client.view.admin;

import Client.view.LoginScreen;
import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class AdminDashboardScreen {
    private final String playerID;
    private final LoginController loginController = new LoginController();

    // Constructor accepting playerID
    public AdminDashboardScreen(String accountID) {
        this.playerID = accountID;

        // Load custom font
        Font customFont = loadCustomFont("../../assets/FVF.ttf"); // Adjust the path to your font file

        // Create JFrame for Admin Dashboard
        JFrame adminFrame = new JFrame("Trang quản trị");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(810, 540);
        adminFrame.setLocationRelativeTo(null);

        // Load GIF background
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../../assets/new_back.gif")); // Path to your GIF file
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout()); // Use GridBagLayout for the background

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make the panel transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Adjust spacing

        // Adjusting button positioning and size
        int buttonWidth = 250;
        int buttonHeight = 40;

        // Title label background panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 169, 114, 180)); // Semi-transparent background
        titlePanel.setLayout(new GridBagLayout());

        // Title label
        JLabel titleLabel = new JLabel("Phân loại rác, vì tương lai xanh !", JLabel.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 12)); // Font size adjusted to 12
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel); // Add titleLabel to titlePanel

        gbc.gridy = 0; // Set to the first row for the title panel
        gbc.gridwidth = 2; // Title panel spans both columns
        panel.add(titlePanel, gbc); // Add titlePanel to the main panel

        // Manage User Accounts button
        JButton btnManageUsers = createCustomButton("Quản lý tài khoản", customFont, buttonWidth, buttonHeight);
        gbc.gridy = 1; // Move to the next row for the button
        gbc.gridwidth = 1; // Reset gridwidth
        panel.add(btnManageUsers, gbc);

        // Item Management button
        JButton btnItemManagement = createCustomButton("Quản lý vật phẩm", customFont, buttonWidth, buttonHeight);
        gbc.gridy = 2; // Move to the next row for the button
        panel.add(btnItemManagement, gbc);

        // Logout button
        JButton btnLogout = createCustomButton("Đăng xuất", customFont, buttonWidth, buttonHeight);
        gbc.gridy = 4; // Move to the next row for the logout button
        gbc.anchor = GridBagConstraints.CENTER; // Keep it centered
        panel.add(btnLogout, gbc);

        // Add Action Listeners to buttons
        btnManageUsers.addActionListener(e -> openUserManagement());
        btnItemManagement.addActionListener(e -> openItemManagement());
        btnLogout.addActionListener(e -> {
            loginController.logout(playerID);
            adminFrame.dispose(); // Close admin frame
            new LoginScreen(); // Open login screen
        });

        // Add panel to background
        backgroundLabel.add(panel);

        // Add background to frame
        adminFrame.add(backgroundLabel);
        adminFrame.setVisible(true);
    }

    // Method to create a custom button
    private JButton createCustomButton(String text, Font customFont, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(customFont.deriveFont(Font.BOLD, 12)); // Use custom font with smaller size
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
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
            return new Font("Serif", Font.PLAIN, 18); // Fallback to default font
        }
    }

    private void openUserManagement() {
        new AccountManagementScreen(); // Implement as needed
    }

    private void openItemManagement() {
        new ItemManagementScreen(); // Implement as needed
    }
}

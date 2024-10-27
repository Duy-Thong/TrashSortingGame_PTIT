package Client.view;

import Client.controller.LoginController;
import Client.view.admin.AdminDashboardScreen;
import Client.model.Account;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class LoginScreen {
    private final LoginController loginController;

    public LoginScreen() {
        loginController = new LoginController();

        // Load custom font
        Font customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to your font file

        // Create JFrame for the login screen
        JFrame loginFrame = new JFrame("Đăng nhập");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(810, 540);
        loginFrame.setLocationRelativeTo(null);

        // Load animated GIF background without scaling
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/back_login.gif"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout()); // Set layout for components over background
        backgroundLabel.setPreferredSize(new Dimension(810, 540)); // Ensure it uses the full size

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Ensure the panel is transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Đăng nhập", JLabel.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 22)); // Use custom font
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        // Username label and field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 16)); // Use custom font
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 14)); // Use custom font
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 16)); // Use custom font
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 14)); // Use custom font
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(passwordField, gbc);

        // Back button
        gbc.gridx = 0; // Move back button to the first cell
        gbc.gridy = 3;
        gbc.gridwidth = 1; // Allow the button to take one grid cell width
        JButton btnBack = createButton("Trở về", customFont, new Color(204, 0, 0));
        panel.add(btnBack, gbc);

        // Login button
        gbc.gridx = 1; // Move login button to the next cell
        JButton btnSubmit = createButton("Đăng nhập", customFont, new Color(0, 102, 204));
        panel.add(btnSubmit, gbc);

        // Add panel to background
        backgroundLabel.add(panel);

        // Add background to frame
        loginFrame.add(backgroundLabel);

        // Back button action
        btnBack.addActionListener(e -> {
            loginFrame.dispose();
            new MainScreen();
        });

        // Login button action
        btnSubmit.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Vui lòng nhập tên đăng nhập và mật khẩu", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Authenticate account
            try {
                if (loginController.authenticate(username, password)) {
                    String accountID = loginController.getAccountIDByUsername(username);
                    String playerID = loginController.getPlayerIDByAccountID(accountID);
                    Account account = loginController.getAccountByAccountID(accountID);

                    String role = account.getRole();
                    if (role.equals("admin")) {
                        new AdminDashboardScreen(accountID);
                        loginFrame.dispose();
                    } else {
                        new LobbyScreen(playerID, username);
                        loginFrame.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Tên đăng nhập hoặc mật khẩu không đúng", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame, "Đã xảy ra lỗi kết nối. Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        loginFrame.setVisible(true);
    }

    // Create a button with custom font and background color
    private JButton createButton(String text, Font font, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(font.deriveFont(Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
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

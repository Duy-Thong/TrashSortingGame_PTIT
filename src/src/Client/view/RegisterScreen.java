package Client.view;

import Client.controller.RegisterController;
import Client.model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

public class RegisterScreen {
    private final RegisterController registerController;

    public RegisterScreen() {
        registerController = new RegisterController();

        // Load custom font
        Font customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to your font file

        // Create JFrame for the registration screen
        JFrame registerFrame = new JFrame("Đăng ký");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(810, 540);
        registerFrame.setLocationRelativeTo(null);

        // Load background image and resize it
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/back_notext.png"));
        Image backgroundImage = backgroundIcon.getImage();
        backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(810, 540, Image.SCALE_SMOOTH));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout());

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Đăng ký", JLabel.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        // Username label and field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18));
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(passwordField, gbc);

        // Confirm password label and field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18));
        confirmPasswordLabel.setForeground(Color.WHITE);
        panel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(customFont.deriveFont(Font.PLAIN, 16));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(confirmPasswordField, gbc);

        // Back button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JButton btnBack = createButton("Trở về", customFont, new Color(204, 0, 0));
        btnBack.addActionListener(e -> {
            registerFrame.dispose();
            new MainScreen(); // Open main screen
        });
        panel.add(btnBack, gbc);

        // Register button
        gbc.gridx = 1;
        JButton btnSubmit = createButton("Đăng ký", customFont, new Color(0, 102, 204));
        panel.add(btnSubmit, gbc);

        // Add panel to background
        backgroundLabel.add(panel);

        // Add background to frame
        registerFrame.add(backgroundLabel);

        // Register button action
        btnSubmit.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerFrame, "Xác nhận mật khẩu không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Register the account
            if (registerController.register(username, password)) {
                JOptionPane.showMessageDialog(registerFrame, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();
                new MainScreen(); // Go back to the main screen
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Đăng ký thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Show the frame
        registerFrame.setVisible(true);
    }

    // Create a button with custom font and background color
    private JButton createButton(String text, Font font, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(font.deriveFont(Font.BOLD, 12));
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

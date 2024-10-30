package Client.view;

import Client.controller.RegisterController;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

        // Load animated GIF background (same as LoginScreen)
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/back_login.gif"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout());
        backgroundLabel.setPreferredSize(new Dimension(810, 540));

        // Main panel with transparent background
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // 50 pixels top margin

        // Add title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Đăng ký", JLabel.CENTER);
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        // Username label and field
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 12));
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 12));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 12));
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 12));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(passwordField, gbc);

        // Confirm password label and field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(customFont.deriveFont(Font.PLAIN, 12));
        confirmPasswordLabel.setForeground(Color.WHITE);
        panel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(customFont.deriveFont(Font.PLAIN, 12));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        panel.add(confirmPasswordField, gbc);

        // Back button
        gbc.gridx = 0;
        gbc.gridy = 4;
        JButton btnBack = createButton("Trở về", customFont, new Color(204, 0, 0));
        panel.add(btnBack, gbc);

        // Register button
        gbc.gridx = 1;
        JButton btnSubmit = createButton("Đăng ký", customFont, new Color(0, 102, 204));
        panel.add(btnSubmit, gbc);

        // Add panel to background
        backgroundLabel.add(panel);

        // Add background to frame
        registerFrame.add(backgroundLabel);

        // Back button action
        btnBack.addActionListener(e -> {
            registerFrame.dispose();
            new MainScreen(); // Open main screen
        });

        // Register button action
        btnSubmit.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Check if any fields are empty
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Password regex pattern
            String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
            Pattern pattern = Pattern.compile(passwordPattern);
            Matcher matcher = pattern.matcher(password);

            // Check if the password meets the regex requirements
            if (!matcher.matches()) {
                JOptionPane.showMessageDialog(registerFrame, "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerFrame, "Xác nhận mật khẩu không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Register the account
            int result = registerController.register(username, password);
            if (result == 1) {
                JOptionPane.showMessageDialog(registerFrame, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();
                new LoginScreen(); // Open login screen
            } else if (result == -1) {
                JOptionPane.showMessageDialog(registerFrame, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
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
        button.setPreferredSize(new Dimension(120, 30));
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

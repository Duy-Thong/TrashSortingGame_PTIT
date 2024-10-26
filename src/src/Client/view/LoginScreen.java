package Client.view;

import Client.controller.LoginController;
import Client.view.admin.AdminDashboardScreen;
import Client.model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;

public class LoginScreen {
    private final LoginController loginController;

    public LoginScreen() {
        loginController = new LoginController();

        // Tạo JFrame cho màn hình đăng nhập
        JFrame loginFrame = new JFrame("Đăng nhập");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(810, 540);
        loginFrame.setLocationRelativeTo(null);

        // Load ảnh nền và resize vừa với chiều rộng của JFrame, giữ tỷ lệ khung hình
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("../assets/back.png"));
        Image backgroundImage = backgroundIcon.getImage();

        // Calculate new height to maintain aspect ratio based on frame's width
        int newWidth = loginFrame.getWidth();
        int newHeight = (int) (backgroundImage.getHeight(null) * ((double) newWidth / backgroundImage.getWidth(null)));
        Image scaledBackgroundImage = backgroundImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        backgroundIcon = new ImageIcon(scaledBackgroundImage);
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new GridBagLayout()); // Để thêm các thành phần vào nền

        // Tạo panel chính
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Để hình nền hiển thị rõ
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thêm tiêu đề lớn
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Đăng nhập", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        // Tên đăng nhập
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        usernameField.setPreferredSize(new Dimension(200, 30)); // Adjusted size for the username field
        panel.add(usernameField, gbc);

        // Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        passwordField.setPreferredSize(new Dimension(200, 30)); // Adjusted size for the password field
        panel.add(passwordField, gbc);

        // Nút đăng nhập
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton btnSubmit = new JButton("Đăng nhập");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 18));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setBackground(new Color(0, 102, 204));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(120, 35)); // Adjusted size for the login button
        panel.add(btnSubmit, gbc);

        // Nút trở về
        gbc.gridy = 4;
        JButton btnBack = new JButton("Trở về");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(204, 0, 0));
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setPreferredSize(new Dimension(120, 35)); // Adjusted size for the back button
        panel.add(btnBack, gbc);

        // Thêm panel vào label nền
        backgroundLabel.add(panel);

        // Thêm hình nền vào JFrame
        loginFrame.add(backgroundLabel);

        // Hành động nút trở về
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginFrame.dispose();
                new MainScreen(); // Quay lại màn hình chính
            }
        });

        // Hành động nút đăng nhập
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "Vui lòng nhập tên đăng nhập và mật khẩu", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Xác thực tài khoản
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
            }
        });

        loginFrame.setVisible(true);
    }
}

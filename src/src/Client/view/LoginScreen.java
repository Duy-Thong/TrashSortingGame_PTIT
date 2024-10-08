package Client.view;

import Client.controller.LoginController;
import Client.model.Account;
import Client.view.admin.MainAdmin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen {
    private final LoginController loginController;

    public LoginScreen() {
        loginController = new LoginController();

        JFrame loginFrame = new JFrame("Đăng nhập");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(810, 540);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 0; // Hàng 0
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1; // Cột 1
        gbc.gridy = 0; // Hàng 0
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JButton btnSubmit = new JButton("Đăng nhập");
        panel.add(btnSubmit, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton btnBack = new JButton("Trở về");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginFrame.dispose();
                new MainScreen(); // Quay lại màn hình chính
            }
        });
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Kiểm tra xem tên đăng nhập và mật khẩu có trống không
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "Vui lòng nhập tên đăng nhập và mật khẩu", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Xác thực tài khoản
                try {
                    if (loginController.authenticate(username, password)) {
                        // Query accountID from username
                        String accountID = loginController.getAccountIDByUsername(username);

                        // Query playerID using accountID
                        String playerID = loginController.getPlayerIDByAccountID(accountID);
                        Account account = loginController.getAccountByAccountID(accountID);

                        loginFrame.dispose(); // Đóng màn hình đăng nhập
                        String role = account.getRole();
                        if (role.equals("admin")) {
                            new MainAdmin(accountID); // Mở
                        } else {
                            new LobbyScreen(playerID); // Mở
                        }
                    } else {
                        JOptionPane.showMessageDialog(loginFrame, "Tên đăng nhập hoặc mật khẩu không đúng", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    // Xử lý ngoại lệ trong quá trình kết nối mạng
                    JOptionPane.showMessageDialog(loginFrame, "Đã xảy ra lỗi kết nối. Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        panel.add(btnBack, gbc);
        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }
}
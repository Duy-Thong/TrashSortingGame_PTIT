package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen {
    public LoginScreen() {
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
                new MainScreen();
            }
        });
        panel.add(btnBack, gbc);
        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }
}
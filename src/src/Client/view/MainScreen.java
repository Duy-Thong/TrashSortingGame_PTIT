package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen {
    public MainScreen() {
        JFrame frame = new JFrame("Màn hình bắt đầu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(810, 540);
        frame.setLocationRelativeTo(null);

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnRegister = new JButton("Đăng ký");

        btnLogin.setPreferredSize(new Dimension(150, 40));
        btnRegister.setPreferredSize(new Dimension(150, 40));

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new LoginScreen();
            }
        });
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                new RegisterScreen();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        panel.add(btnLogin);
        panel.add(btnRegister);
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen());
    }
}
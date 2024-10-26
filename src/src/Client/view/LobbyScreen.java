package Client.view;

import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyScreen {
    private final String playerID; // Add playerID field
    private final String username;
    private final LoginController loginController = new LoginController();

    // Constructor accepting playerID
    public LobbyScreen(String playerID, String username) {
        this.playerID = playerID;
        this.username = username;

        // Tạo JFrame cho màn hình lobby
        JFrame lobbyFrame = new JFrame("Lobby");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize(810, 540);
        lobbyFrame.setLocationRelativeTo(null);

        // Tạo panel chính
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Adjusting the spacing between buttons

        // Nút Trang cá nhân (hình vuông)
        JButton btnProfile = new JButton("Trang cá nhân");
        btnProfile.setPreferredSize(new Dimension(150, 150)); // Nút hình vuông
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(btnProfile, gbc);

        // Nút Xem lịch sử
        JButton btnHistory = new JButton("Xem lịch sử");
        btnHistory.setPreferredSize(new Dimension(150, 50));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(btnHistory, gbc);

        // Nút Xem bảng xếp hạng
        JButton btnRanking = new JButton("Xem bảng xếp hạng");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(btnRanking, gbc);

        // Nút Vào game
        JButton btnEnterGame = new JButton("Vào game");
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(btnEnterGame, gbc);

        // Nút Đăng xuất
        JButton btnLogout = new JButton("Đăng xuất");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make the button span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogout, gbc);

        // Hành động khi bấm nút Trang cá nhân
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Trang cá nhân và in ra playerID
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Xem Profile cá nhân");
                new ProfileScreen(playerID);
            }
        });

        // Hành động khi bấm nút Xem lịch sử
        btnHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Xem lịch sử
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Xem lịch sử người chơi");
                new HistoryScreen(playerID);
            }
        });

        // Hành động khi bấm nút Xem bảng xếp hạng
        btnRanking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Bảng xếp hạng
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Bảng xếp hạng");
                new RankScreen();
            }
        });

        // Hành động khi bấm nút Vào game
        btnEnterGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình game
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Game");
                new InviteScreen(playerID, username);
            }
        });

        // Hành động khi bấm nút Đăng xuất
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển về màn hình đăng nhập
                loginController.logout(playerID);
                lobbyFrame.dispose(); // Đóng màn hình lobby
                new LoginScreen(); // Mở lại màn hình đăng nhập
            }
        });

        // Thêm panel vào frame và hiển thị màn hình
        lobbyFrame.add(panel);
        lobbyFrame.setVisible(true);
    }
}

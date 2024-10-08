package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyScreen {
    public LobbyScreen() {
        // Tạo JFrame cho màn hình lobby
        JFrame lobbyFrame = new JFrame("Lobby");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize(810, 540);
        lobbyFrame.setLocationRelativeTo(null);

        // Tạo panel chính
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Nút Trang cá nhân (hình vuông)
        JButton btnProfile = new JButton("Trang cá nhân");
        btnProfile.setPreferredSize(new Dimension(150, 150)); // Nút hình vuông
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnProfile, gbc);

        // Nút Xem lịch sử
        JButton btnHistory = new JButton("Xem lịch sử");
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

        // Hành động khi bấm nút Trang cá nhân
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Trang cá nhân
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Trang cá nhân");
            }
        });

        // Hành động khi bấm nút Xem lịch sử
        btnHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Xem lịch sử
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Xem lịch sử");
            }
        });

        // Hành động khi bấm nút Xem bảng xếp hạng
        btnRanking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình Bảng xếp hạng
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Bảng xếp hạng");
            }
        });

        // Hành động khi bấm nút Vào game
        btnEnterGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chuyển đến màn hình game
                JOptionPane.showMessageDialog(lobbyFrame, "Chuyển đến Game");
            }
        });

        // Thêm panel vào frame và hiển thị màn hình
        lobbyFrame.add(panel);
        lobbyFrame.setVisible(true);
    }
}

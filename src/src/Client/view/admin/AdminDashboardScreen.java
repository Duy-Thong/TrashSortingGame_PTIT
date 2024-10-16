package Client.view.admin;

import Client.view.LoginScreen;
import Client.controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardScreen {
    private String playerID; // Store the admin's account ID
    private final LoginController loginController = new LoginController();
    public AdminDashboardScreen(String accountID) {
        this.playerID = accountID;

        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(810, 540);
        adminFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Admin Dashboard"), gbc);

        // Button to manage user accounts
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JButton btnManageUsers = new JButton("Manage User Accounts");
        btnManageUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open user management window
                openUserManagement();
            }
        });
        panel.add(btnManageUsers, gbc);

        // Button to view logs
        gbc.gridy = 2;
        JButton btnViewLogs = new JButton("Item Management");
        btnViewLogs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open log viewing window
                openItemManagement();
            }
        });
        panel.add(btnViewLogs, gbc);

        // Button to log out
        gbc.gridy = 3;
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close admin frame and go back to login screen
                loginController.logout(playerID);
                adminFrame.dispose();
                new LoginScreen();
            }
        });
        panel.add(btnLogout, gbc);

        adminFrame.add(panel);
        adminFrame.setVisible(true);
    }

    private void openUserManagement() {
        // Implement user management logic here
        new AccountManagementScreen();
    }

    private void openItemManagement() {
        // Implement log viewer logic here
//        new ItemManagementScreen();
    }
}
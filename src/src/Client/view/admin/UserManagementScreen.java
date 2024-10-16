package Client.view.admin;

import Client.controller.admin.UserManagementController;
import Client.model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementScreen extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserManagementController userController;

    public UserManagementScreen() {
        userController = new UserManagementController();

        setTitle("User Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Create table for user data
        String[] columnNames = {"User ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Add components to frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle add user action
                // Example: new AddUserDialog(UserManagementScreen.this).setVisible(true);
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle edit user action
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userID = (String) tableModel.getValueAt(selectedRow, 0);
                    // Example: new EditUserDialog(UserManagementScreen.this, userID).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(UserManagementScreen.this, "Please select a user to edit.");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle delete user action
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userID = (String) tableModel.getValueAt(selectedRow, 0);
                    // Example: deleteUser(userID);
                } else {
                    JOptionPane.showMessageDialog(UserManagementScreen.this, "Please select a user to delete.");
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle refresh action
                loadUserData();
            }
        });

        // Load initial user data
        loadUserData();

        setVisible(true);
    }

    private void loadUserData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Fetch user data from server
        List<Account> accounts = userController.getAllAccount();

        // Add data to model
        for (Account account: accounts) {
            tableModel.addRow(new Object[]{account.getAccountID(), account.getUsername(), account.getRole()});
        }
    }
}
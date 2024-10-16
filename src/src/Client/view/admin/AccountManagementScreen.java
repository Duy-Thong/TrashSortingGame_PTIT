package Client.view.admin;

import Client.controller.admin.AccountManagementController;
import Client.model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AccountManagementScreen extends JFrame {
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountManagementController accountManagementController;

    public AccountManagementScreen() {
        setTitle("Account Management");
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

        // Create table for account data
        String[] columnNames = {"Account ID", "Username", "Password", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        accountTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(accountTable);

        // Add components to frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle add account action
                // Example: new AddAccountDialog(AccountManagementScreen.this).setVisible(true);
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle edit account action
                int selectedRow = accountTable.getSelectedRow();
                if (selectedRow != -1) {
                    String accountID = (String) tableModel.getValueAt(selectedRow, 0);
                    // Example: new EditAccountDialog(AccountManagementScreen.this, accountID).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementScreen.this, "Please select an account to edit.");
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle delete account action
                int selectedRow = accountTable.getSelectedRow();
                if (selectedRow != -1) {
                    String accountID = (String) tableModel.getValueAt(selectedRow, 0);
                    // Example: deleteAccount(accountID);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementScreen.this, "Please select an account to delete.");
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle refresh action
                loadAccountData();
            }
        });

        // Load initial account data
        loadAccountData();

        setVisible(true);
    }

    private void loadAccountData() {
        List<Account> accountList = accountManagementController.getAllAccount();
        tableModel.setRowCount(0);
        for (Account account : accountList) {
            tableModel.addRow(new Object[]{account.getAccountID(), account.getUsername(), account.getPassword(), account.getRole()});
        }
    }
}
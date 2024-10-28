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
        setTitle("Quản lý tài khoản");
        setSize(800, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Tải lại");
        JButton btnBack = new JButton("Quay lại");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);

        // Create table for account data
        String[] columnNames = {"Mã tài khoản", "Tên đăng nhập", "Mật khẩu", "Vai trò"};
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
                new AddAccountDialog(AccountManagementScreen.this).setVisible(true);
                loadAccountData();
            }
        });
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle edit account action
                int selectedRow = accountTable.getSelectedRow();
                if (selectedRow != -1) {
                    String accountID = (String) tableModel.getValueAt(selectedRow, 0);
                    new EditAccountDialog(AccountManagementScreen.this, accountID).setVisible(true);
                    loadAccountData();
                } else {
                    JOptionPane.showMessageDialog(AccountManagementScreen.this, "Vui lòng chọn một tài khoản để sửa.");
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
                    deleteAccount(accountID);
                } else {
                    JOptionPane.showMessageDialog(AccountManagementScreen.this, "Vui lòng chọn một tài khoản để xóa.");
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

    private void deleteAccount(String accountID) {
        accountManagementController.deleteAccount(accountID);
        loadAccountData();
    }
}

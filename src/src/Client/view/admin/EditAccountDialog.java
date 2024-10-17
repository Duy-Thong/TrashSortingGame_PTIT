package Client.view.admin;

import Client.controller.admin.AccountManagementController;
import Client.model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditAccountDialog extends JDialog {
    private JTextField txtAccountID;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JComboBox<String> comboRole;
    private AccountManagementController accountManagementController;
    private String accountID;

    public EditAccountDialog(JFrame parent, String accountID) {
        super(parent, "Edit Account", true);
        this.accountID = accountID;

        accountManagementController = new AccountManagementController();
        Account account = accountManagementController.getAccountByID(accountID);

        setLayout(new BorderLayout());

        // Create form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Account ID:"));
        txtAccountID = new JTextField(account.getAccountID());
        txtAccountID.setEditable(false);  // ID không được chỉnh sửa
        formPanel.add(txtAccountID);

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField(account.getUsername());
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JTextField(account.getPassword());
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Role:"));
        comboRole = new JComboBox<>(new String[]{"admin", "player"});
        comboRole.setSelectedItem(account.getRole());
        formPanel.add(comboRole);

        add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAccount();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog
            }
        });

        pack();
        setLocationRelativeTo(parent);  // Center on parent frame
    }

    private void saveAccount() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = (String) comboRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        accountManagementController.updateAccount(accountID, username, password, role);
        JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();  // Close the dialog after saving
    }
}
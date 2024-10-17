package Client.view.admin;

import Client.controller.admin.AccountManagementController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddAccountDialog extends JDialog {
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JComboBox<String> comboRole;
    private AccountManagementController accountManagementController;

    public AddAccountDialog(JFrame parent) {
        super(parent, "Add Account", true);
        accountManagementController = new AccountManagementController();

        setLayout(new BorderLayout());

        // Create form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JTextField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Role:"));
        comboRole = new JComboBox<>(new String[]{"admin", "player"});
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

        // Assuming the AccountManagementController has a method to add a new account
        boolean success = accountManagementController.addAccount(username, password, role);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();  // Close the dialog after saving
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
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
        super(parent, "Thêm tài khoản", true);
        accountManagementController = new AccountManagementController();

        setLayout(new BorderLayout());

        // Create form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Tên tài khoản:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField(); // Changed to JPasswordField for better security
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Vai trò:"));
        comboRole = new JComboBox<>(new String[]{"admin", "player"});
        formPanel.add(comboRole);

        add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

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
        setResizable(false); // Optional: Prevent resizing
    }

    private void saveAccount() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = (String) comboRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản và Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Assuming the AccountManagementController has a method to add a new account
        int result = accountManagementController.addAccount(username, password, role);
        if (result == 1) {
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();  // Close the dialog
        } else if (result == -1) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

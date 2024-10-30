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
    private JPasswordField txtPassword; // Sử dụng JPasswordField cho mật khẩu
    private JComboBox<String> comboRole;
    private AccountManagementController accountManagementController;
    private String accountID;

    public EditAccountDialog(JFrame parent, String accountID) {
        super(parent, "Sửa tài khoản", true);
        this.accountID = accountID;

        accountManagementController = new AccountManagementController();
        Account account = accountManagementController.getAccountByID(accountID);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose(); // Đóng hộp thoại nếu không tìm thấy tài khoản
            return;
        }

        setLayout(new BorderLayout());

        // Tạo form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Account ID:"));
        txtAccountID = new JTextField(account.getAccountID());
        txtAccountID.setEditable(false);  // ID không được chỉnh sửa
        formPanel.add(txtAccountID);

        formPanel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField(account.getUsername());
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField(account.getPassword()); // Sử dụng JPasswordField
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Vai trò:"));
        comboRole = new JComboBox<>(new String[]{"admin", "player"});
        comboRole.setSelectedItem(account.getRole());
        formPanel.add(comboRole);

        add(formPanel, BorderLayout.CENTER);

        // Tạo panel cho nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Thêm Action Listener cho nút
        btnSave.addActionListener(e -> saveAccount());
        btnCancel.addActionListener(e -> dispose());  // Đóng hộp thoại

        pack();
        setLocationRelativeTo(parent);  // Căn giữa trên khung cha
    }

    private void saveAccount() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Assuming the AccountManagementController has a method to update an account
        if (accountManagementController.updateAccount(accountID, username, password, role)==1) {
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();  // Đóng hộp thoại sau khi cập nhật thành công
        } else if (accountManagementController.updateAccount(accountID, username, password, role)==-1) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

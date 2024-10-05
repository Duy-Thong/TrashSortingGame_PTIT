package Client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterScreen {
    public RegisterScreen() {
        // Tạo frame cho màn hình Đăng ký với kích thước 810x540
        JFrame registerFrame = new JFrame("Đăng ký");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(810, 540); // Kích thước cửa sổ 810x540
        registerFrame.setLocationRelativeTo(null); // Đặt vị trí cửa sổ ở giữa màn hình

        // Tạo panel
        JPanel panel = new JPanel(new GridBagLayout()); // Sử dụng GridBagLayout để căn giữa
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần

        // Thêm các trường nhập liệu
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 0; // Hàng 0
        panel.add(new JLabel("Tên đăng nhập:"), gbc); // Thêm nhãn tên đăng nhập

        gbc.gridx = 1; // Cột 1
        gbc.gridy = 0; // Hàng 0
        JTextField usernameField = new JTextField(15); // Thay đổi kích thước trường nhập liệu
        panel.add(usernameField, gbc); // Thêm trường tên đăng nhập

        gbc.gridx = 0; // Cột 0
        gbc.gridy = 1; // Hàng 1
        panel.add(new JLabel("Mật khẩu:"), gbc); // Thêm nhãn mật khẩu

        gbc.gridx = 1; // Cột 1
        gbc.gridy = 1; // Hàng 1
        JPasswordField passwordField = new JPasswordField(15); // Thay đổi kích thước trường nhập liệu
        panel.add(passwordField, gbc); // Thêm trường mật khẩu

        gbc.gridx = 0; // Cột 0
        gbc.gridy = 2; // Hàng 2
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc); // Thêm nhãn xác nhận mật khẩu

        gbc.gridx = 1; // Cột 1
        gbc.gridy = 2; // Hàng 2
        JPasswordField confirmPasswordField = new JPasswordField(15); // Thay đổi kích thước trường nhập liệu
        panel.add(confirmPasswordField, gbc); // Thêm trường xác nhận mật khẩu

        // Nút Đăng ký
        gbc.gridx = 0; // Cột 0
        gbc.gridy = 3; // Hàng 3
        JButton btnSubmit = new JButton("Đăng ký");
        panel.add(btnSubmit, gbc); // Thêm nút Đăng ký

        // Nút Trở về
        gbc.gridx = 1; // Cột 1
        gbc.gridy = 3; // Hàng 3
        JButton btnBack = new JButton("Trở về");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame.dispose(); // Đóng màn hình Đăng ký
                new MainScreen(); // Mở lại màn hình chính
            }
        });
        panel.add(btnBack, gbc); // Thêm nút Trở về

        // Thêm panel vào frame
        registerFrame.add(panel);

        // Hiển thị frame
        registerFrame.setVisible(true);
    }
}

package Client.view;

import Client.controller.ProfileController;
import Client.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

public class ProfileScreen extends JFrame {
    private JButton backButton;
    private JButton editButton;
    private String playerID;
    private ProfileController profileController;

    public ProfileScreen(String playerID) {
        this.playerID = playerID;
        this.profileController = new ProfileController();

        setTitle("Thông tin cá nhân");
        setSize(810, 540);  // Kích thước cửa sổ
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo các thành phần giao diện
        createBackButton();
        createEditButton();
        JPanel titlePanel = createTitlePanel();  // Sử dụng titlePanel thay vì chỉ titleLabel
        JPanel infoPanel = createInfoPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        // Thiết lập layout
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);  // Thêm titlePanel (chứa khoảng cách)
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện khi nhấn nút Trở về
        backButton.addActionListener(e -> dispose());
        // Xử lý sự kiện khi nhấn nút Chỉnh sửa

        editButton.addActionListener(e -> showEditDialog());

        setVisible(true);
    }

    private void createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
    }

    private void createEditButton() {
        editButton = new JButton("Chỉnh sửa");
        editButton.setPreferredSize(new Dimension(150, 40));
    }

    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Thông tin cá nhân", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Tạo một panel để chứa titleLabel và thêm khoảng cách bên trên
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());  // Sử dụng GridBagLayout để căn chỉnh các nhãn và giá trị
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);  // Padding giữa các thành phần

        // Lấy và hiển thị thông tin từ server
        loadProfileData(infoPanel, gbc);

        return infoPanel;
    }

    private void loadProfileData(JPanel infoPanel, GridBagConstraints gbc) {
        ProfileController profileController = new ProfileController();  // Khởi tạo controller
        Player player = profileController.getPlayerProfile(playerID);  // Lấy dữ liệu profile từ controller
        if (player != null) {
            // Hiển thị thông tin cá nhân với hai cột nhãn và giá trị
            addLabelAndValue(infoPanel, gbc, "Tên người chơi:", player.getUsername(), 0);
            addLabelAndValue(infoPanel, gbc, "Tổng số trận:", String.valueOf(player.getTotalGames()), 1);
            addLabelAndValue(infoPanel, gbc, "Tổng bàn thắng:", String.valueOf(player.getTotalWins()), 2);
            addLabelAndValue(infoPanel, gbc, "Tổng điểm:", String.valueOf(player.getTotalScore()), 3);
            addLabelAndValue(infoPanel, gbc, "Điểm trung bình:", String.valueOf(player.getAverageScore()), 4);
            // Hiển thị thời gian tạo tài khoản (createdAt)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createdAtStr = dateFormat.format(player.getCreatedAt());

            addLabelAndValue(infoPanel, gbc, "Ngày tạo:", createdAtStr, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cá nhân.");
        }
    }

    private void addLabelAndValue(JPanel panel, GridBagConstraints gbc, String labelText, String valueText, int row) {
        gbc.gridx = 0;  // Cột đầu tiên cho nhãn
        gbc.gridy = row;  // Dòng hiện tại
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;  // Cột thứ hai cho giá trị
        JLabel valueLabel = new JLabel(valueText);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueLabel, gbc);
    }

    private void showEditDialog() {
        Player player = profileController.getPlayerProfile(playerID);  // Lấy thông tin người chơi hiện tại
        if (player == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cá nhân.");
            return;
        }
        // Tạo panel cho hộp thoại chỉnh sửa
        JPanel editPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JTextField usernameField = new JTextField(player.getUsername());  // Điền sẵn tên người dùng hiện tại
        JPasswordField passwordField = new JPasswordField();
        editPanel.add(new JLabel("Tên người dùng:"));
        editPanel.add(usernameField);
        editPanel.add(new JLabel("Mật khẩu:"));
        editPanel.add(passwordField);
        // Hiển thị hộp thoại
        int result = JOptionPane.showOptionDialog(
                this,
                editPanel,
                "Chỉnh sửa thông tin",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Lưu", "Hủy"},
                "Lưu"
        );
        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            // Cập nhật thông tin người chơi
            if (!newUsername.isEmpty()) {
                updatePlayerInfo(newUsername, newPassword);
            } else {
                JOptionPane.showMessageDialog(this, "Tên người dùng không được để trống.");
            }
        }
    }
    private void updatePlayerInfo(String username, String password) {
        // Gọi hàm cập nhật thông tin trong ProfileController
        String response = profileController.updatePlayerProfile(playerID, username, password);
        if (response.startsWith("success")) {
            JOptionPane.showMessageDialog(this, "Thông tin đã được cập nhật thành công!");
        } else if (response.startsWith("error: username already exists")) {
            JOptionPane.showMessageDialog(this, "Tên người dùng đã được sử dụng, vui lòng chọn tên khác!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật thông tin.");
        }
    }

    public JButton getBackButton() {
        return backButton;
    }
}

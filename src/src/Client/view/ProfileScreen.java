package Client.view;

import Client.controller.ProfileController;
import Client.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileScreen extends JFrame {
    private JButton backButton;
    private String playerID;

    public ProfileScreen(String playerID) {
        this.playerID = playerID;

        setTitle("Thông tin cá nhân");
        setSize(810, 540);  // Kích thước cửa sổ
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo các thành phần giao diện
        createBackButton();
        JPanel titlePanel = createTitlePanel();  // Sử dụng titlePanel thay vì chỉ titleLabel
        JPanel infoPanel = createInfoPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        // Thiết lập layout
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);  // Thêm titlePanel (chứa khoảng cách)
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện khi nhấn nút Trở về
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Đóng cửa sổ hiện tại
                // new LobbyScreen();  // Gọi LobbyScreen nếu đã có (bạn cần thay đổi phần này tùy vào project)
            }
        });

        setVisible(true);
    }

    private void createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
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

    public JButton getBackButton() {
        return backButton;
    }
}

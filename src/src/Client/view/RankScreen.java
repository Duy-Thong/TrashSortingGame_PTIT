package Client.view;

import Client.controller.RankController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RankScreen extends JFrame {
    private RankController rankController;
    private JTable rankTable;

    public RankScreen() {
        this.rankController = new RankController();

        setTitle("Bảng xếp hạng người chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Thêm tiêu đề
        add(createTitlePanel(), BorderLayout.NORTH);

        // Tạo bảng xếp hạng
        String[] columnNames = {"Top", "Tên người chơi", "Tổng điểm", "Tổng bàn thắng", "Tổng số trận"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa ô trong bảng
            }
        };

        rankTable = new JTable(model);
        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Không cho phép chọn hàng

        // Lấy danh sách người chơi xếp hạng cao và thêm vào bảng
        List<Player> topPlayers = rankController.getRank();
        int stt = 1; // Khởi tạo STT từ 1
        for (Player player : topPlayers) {
            model.addRow(new Object[]{
                    stt++, // STT tự động tăng
                    player.getUsername(),
                    player.getTotalScore(),
                    player.getTotalWins(),
                    player.getTotalGames()
            });
        }

        // Tạo panel cho bảng và thêm khoảng cách 10px trên bảng
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Khoảng cách 10px trên bảng

        // Sử dụng JScrollPane để bao bảng, tự động hiển thị thanh cuộn nếu vượt kích thước
        JScrollPane scrollPane = new JScrollPane(rankTable);
        scrollPane.setPreferredSize(new Dimension(780, 400)); // Đặt kích thước mong muốn cho bảng

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Thêm nút "Trở về"
        add(createBackButton(), BorderLayout.SOUTH);

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Tạo nút "Trở về"
    private JPanel createBackButton() {
        JButton backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));

        // Tạo một panel để chứa nút và căn giữa
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);

        // Xử lý sự kiện khi nhấn nút trở về (có thể đóng cửa sổ hiện tại)
        backButton.addActionListener(e -> dispose());

        return buttonPanel;
    }

    // Tạo panel tiêu đề
    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Bảng xếp hạng người chơi", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Tạo một panel để chứa titleLabel và thêm khoảng cách bên trên
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }
}

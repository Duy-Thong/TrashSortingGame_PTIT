package Client.view;

import Client.controller.HistoryController;
import Client.model.PlayerGame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryScreen extends JFrame {
    private String playerId; // Player ID được truyền từ màn hình trước
    private HistoryController historyController;
    private JButton backButton; // Nút Trở về

    public HistoryScreen(String playerId) {
        this.playerId = playerId;
        this.historyController = new HistoryController();

        // Đặt tiêu đề cho cửa sổ
        setTitle("Lịch sử người chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Thêm tiêu đề
        add(createTitlePanel(), BorderLayout.NORTH);

        // Tạo bảng để hiển thị lịch sử
        String[] columnNames = {"STT", "Game ID", "Thời gian tham gia", "Điểm số", "Kết quả"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa ô trong bảng
            }
        };

        JTable historyTable = new JTable(model);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Không cho phép chọn hàng

        // Lấy lịch sử người chơi và thêm vào bảng
        List<PlayerGame> historyList = historyController.getPlayerHistory(playerId);
        int stt = 1; // Khởi tạo STT từ 1
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Định dạng thời gian

        for (PlayerGame game : historyList) {
            // Chuyển đổi thời gian tham gia từ Date sang String
            String formattedJoinTime = sdf.format(game.getJoinTime());

            model.addRow(new Object[]{
                    stt++, // STT tự động tăng
                    game.getGameID(),
                    formattedJoinTime, // Sử dụng thời gian đã được định dạng
                    game.getScore(),
                    game.getResult()
            });
        }

        // Tạo một panel chứa bảng với khoảng cách
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Khoảng cách 10px giữa title và bảng

        // Bọc bảng bằng JScrollPane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm panel bảng vào cửa sổ
        add(tablePanel, BorderLayout.CENTER);

        // Thêm nút Trở về
        add(createBackButton(), BorderLayout.SOUTH);

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);

        // Xử lý sự kiện khi nhấn nút Trở về
        backButton.addActionListener(e -> {
            dispose();  // Đóng cửa sổ hiện tại
            // new LobbyScreen();  // Gọi LobbyScreen nếu đã có (bạn cần thay đổi phần này tùy vào project)
        });
    }

    // Tạo nút Trở về
    private JPanel createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));

        // Tạo một panel để chứa nút và căn giữa
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // Tạo panel tiêu đề
    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Lịch sử người chơi", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Tạo một panel để chứa titleLabel và thêm khoảng cách bên trên
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    public JButton getBackButton() {
        return backButton;
    }
}

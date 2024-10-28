package Client.view;

import Client.controller.HistoryController;
import Client.model.PlayerGame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryScreen extends JFrame {
    private String playerId; // Player ID được truyền từ màn hình trước
    private HistoryController historyController;
    private JButton backButton; // Nút Trở về
    Font pixelFont;

    public HistoryScreen(String playerId, String username) {
        this.playerId = playerId;
        this.historyController = new HistoryController();

        // Tải font pixel
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("Client/assets/FVF.ttf");
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(10f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pixelFont);
            } else {
                System.out.println("Tệp phông chữ không được tìm thấy.");
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // Đặt tiêu đề cho cửa sổ
        setTitle("Lịch sử người chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sử dụng BackgroundPanel cho ảnh nền
        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby.gif")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        // Tạo panel tiêu đề
        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

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
        historyTable.setFont(pixelFont); // Đặt font cho bảng

        // Thiết lập font cho tiêu đề cột
        historyTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));

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

        // Bọc bảng bằng JScrollPane và thêm khoảng cách 5px bên trái và phải
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Tạo JPanel để bao quanh scrollPane và thêm khoảng cách bên trong
        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createEmptyBorder(0, 55, 5, 55)); // Khoảng cách 5px bên trái và phải
        panelTable.setOpaque(false); // Đặt không có màu nền để hòa vào ảnh nền
        panelTable.add(scrollPane, BorderLayout.CENTER); // Thêm JScrollPane vào panelTable

        backgroundPanel.add(panelTable, BorderLayout.CENTER); // Thêm panelTable vào backgroundPanel

        // Tạo nút Trở về
        JPanel buttonPanel = createBackButton();
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH); // Thêm nút vào backgroundPanel

        // Thêm backgroundPanel vào cửa sổ
        add(backgroundPanel);

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);

        // Xử lý sự kiện khi nhấn nút Trở về
        backButton.addActionListener(e -> {
            dispose();
            new LobbyScreen(playerId, username);
        });
    }

    // Tạo panel tiêu đề
    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Lịch sử người chơi", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.WHITE);

        // Tạo một panel để chứa titleLabel và thêm khoảng cách bên trên
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setOpaque(false);

        return titlePanel;
    }

    // Tạo nút Trở về
    private JPanel createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(pixelFont.deriveFont(12f));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tạo một panel để chứa nút và căn giữa
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // Tạo lớp BackgroundPanel để vẽ ảnh nền
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

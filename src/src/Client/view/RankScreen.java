package Client.view;

import Client.controller.RankController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RankScreen extends JFrame {
    private RankController rankController;
    private JTable rankTable;
    private JButton backButton;
    Font pixelFont;

    public RankScreen(String playerId, String username) {
        this.rankController = new RankController();

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

        setTitle("Bảng xếp hạng người chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sử dụng BackgroundPanel cho ảnh nền
        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby.gif")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        // Thêm tiêu đề
        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // Tạo bảng xếp hạng
        String[] columnNames = {"Top", "Tên người chơi", "Tổng điểm", "Tổng bàn thắng", "Tổng số trận"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankTable = new JTable(model);
        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankTable.setFont(pixelFont);

        rankTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));

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
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 55, 5, 55));

        // Sử dụng JScrollPane để bao bảng, tự động hiển thị thanh cuộn nếu vượt kích thước
        JScrollPane scrollPane = new JScrollPane(rankTable);
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(tablePanel, BorderLayout.CENTER);

        // Thêm nút "Trở về"
        JPanel buttonPanel = createBackButton();
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);

        // Xử lý sự kiện khi nhấn nút Trở về
        backButton.addActionListener(e -> {
            dispose();
            new LobbyScreen(playerId, username);
        });

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Tạo nút "Trở về"
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

    // Tạo panel tiêu đề
    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Bảng xếp hạng người chơi", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.WHITE);

        // Tạo một panel để chứa titleLabel và thêm khoảng cách bên trên
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setOpaque(false);

        return titlePanel;
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

package Client.view;

import Client.controller.HistoryController;
import Client.model.PlayerGame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryScreen extends JFrame {
    private String playerId;
    private HistoryController historyController;
    private JButton backButton;
    Font pixelFont;

    public HistoryScreen(String playerId, String username) {
        this.playerId = playerId;
        this.historyController = new HistoryController();

        // Tải phông chữ
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

        setTitle("Lịch sử người chơi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_notext.jpg")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"STT", "Thời gian tham gia", "Điểm số", "Kết quả"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa ô trong bảng
            }
        };

        JTable historyTable = new JTable(model);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);
        historyTable.setFont(pixelFont);
        historyTable.setBackground(new Color(255, 255, 255, 128));
        historyTable.setGridColor(Color.WHITE);

        historyTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        historyTable.getTableHeader().setForeground(Color.BLACK);
        historyTable.getTableHeader().setBackground(new Color(255, 255, 255)); // nếu muốn nền trắng

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(pixelFont.deriveFont(Font.BOLD, 12f));  // Đặt font chữ đậm ở đây
                label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE)); // Đặt viền trắng
                return label;
            }
        };

        for (int i = 0; i < historyTable.getColumnModel().getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnNames.length; i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Lấy lịch sử người chơi
        List<PlayerGame> historyList = historyController.getPlayerHistory(playerId);
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (PlayerGame game : historyList) {
            String formattedJoinTime = sdf.format(game.getJoinTime());
            model.addRow(new Object[]{
                    stt++,
                    formattedJoinTime,
                    game.getScore(),
                    game.getResult()
            });
        }

        int maxRows = 14;
        int currentRows = historyList.size();
        for (int i = currentRows; i < maxRows; i++) {
            model.addRow(new Object[]{
                    "",
                    "",
                    "",
                    ""
            });
        }

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new LineBorder(Color.WHITE, 2));

        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createEmptyBorder(0, 55, 5, 55));
        panelTable.setOpaque(false);
        panelTable.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(panelTable, BorderLayout.CENTER);

        JPanel buttonPanel = createBackButton();
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);

        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);

        backButton.addActionListener(e -> {
            dispose();
            new LobbyScreen(playerId, username);
        });
    }

    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Lịch sử người chơi", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));  // Khoảng cách 10px bên trên
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setOpaque(false);

        return titlePanel;
    }

    private JPanel createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(pixelFont.deriveFont(12f));
        backButton.setBackground(new Color(204, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

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

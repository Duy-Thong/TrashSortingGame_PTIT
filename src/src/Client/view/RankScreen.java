package Client.view;

import Client.controller.RankController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RankScreen extends JFrame {
    private RankController rankController;
    private JTable rankTable;
    private JButton backButton;
    private Font pixelFont;

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

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby1.jpg")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"Top", "Tên người chơi", "Tổng điểm", "Tổng bàn thắng", "Tổng số trận"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankTable = new JTable(model);
        rankTable.setRowHeight(25);
        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankTable.setFont(pixelFont);
        rankTable.setBackground(new Color(255, 255, 255, 128));
        rankTable.setGridColor(Color.WHITE);

        // Thiết lập font chữ và màu cho phần header của bảng
        rankTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        rankTable.getTableHeader().setForeground(Color.BLACK);
        rankTable.getTableHeader().setBackground(new Color(255, 255, 255)); // nếu muốn nền trắng

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

        for (int i = 0; i < rankTable.getColumnModel().getColumnCount(); i++) {
            rankTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnNames.length; i++) {
            rankTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        List<Player> topPlayers = rankController.getRank();
        int stt = 1;
        for (Player player : topPlayers) {
            model.addRow(new Object[]{
                    stt++,
                    player.getUsername(),
                    player.getTotalScore(),
                    player.getTotalWins(),
                    player.getTotalGames()
            });
        }

        int maxRows = 14;
        int currentRows = topPlayers.size();
        for (int i = currentRows; i < maxRows; i++) {
            model.addRow(new Object[]{
                    "",
                    "",
                    "",
                    ""
            });
        }

        JScrollPane scrollPane = new JScrollPane(rankTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new LineBorder(Color.WHITE, 2));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 55, 5, 55));
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createBackButton();
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);

        backButton.addActionListener(e -> {
            dispose();
            new LobbyScreen(playerId, username);
        });

        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);
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

    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Bảng xếp hạng người chơi", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setOpaque(false);

        return titlePanel;
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

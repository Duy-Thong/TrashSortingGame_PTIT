package Client.view;

import Client.controller.RankController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
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

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_notext.jpg")).getImage());
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

        rankTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (component instanceof JComponent) {
                    ((JComponent) component).setOpaque(false); // Đặt các ô có dữ liệu trong suốt
                }
                return component;
            }
        };

        // Set the row height to make rows taller
        rankTable.setRowHeight(25); // Set the desired height (e.g., 30 pixels)

        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankTable.setFont(pixelFont);
        rankTable.setOpaque(false); // Đặt bảng trong suốt
        rankTable.setBackground(new Color(0, 0, 0, 0)); // Đặt nền bảng trong suốt
        rankTable.setGridColor(Color.WHITE); // Đặt màu viền giữa các ô

        rankTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        rankTable.getTableHeader().setOpaque(false); // Đặt tiêu đề bảng trong suốt
        rankTable.getTableHeader().setBackground(null); // Đặt nền tiêu đề bảng trong suốt
        rankTable.getTableHeader().setForeground(Color.BLACK); // Đặt màu chữ tiêu đề trắng

        // Thiết lập màu chữ trắng cho nội dung của bảng
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setForeground(Color.WHITE);
        rankTable.setDefaultRenderer(Object.class, cellRenderer);

        // Căn giữa cho tất cả các cột
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

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 55, 5, 55));
        JScrollPane scrollPane = new JScrollPane(rankTable);
        scrollPane.setOpaque(false); // Đặt JScrollPane trong suốt
        scrollPane.getViewport().setOpaque(false); // Đặt viewport của JScrollPane trong suốt
        scrollPane.setBorder(new LineBorder(Color.WHITE, 2)); // Đặt viền trắng cho bảng

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
        titleLabel.setForeground(Color.WHITE);

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

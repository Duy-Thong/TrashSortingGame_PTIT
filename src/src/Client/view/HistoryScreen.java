package Client.view;

import Client.controller.HistoryController;
import Client.model.PlayerGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryScreen extends JFrame {
    private String playerId;
    private HistoryController historyController;
    private JButton backButton;
    private final int widthImage = 60;
    private final int heightImage = 30;
    Font pixelFont;

    public HistoryScreen(String playerId, String username) {
        this.playerId = playerId;
        this.historyController = new HistoryController();

        // Load font
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("Client/assets/FVF.ttf");
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(10f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pixelFont);
            } else {
                System.out.println("Font file not found.");
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        setTitle("Player History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby1.jpg")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // Updated table columns
        String[] columnNames = {"STT", "Thời gian", "Điểm", "Kết quả", "Đối thủ", "Điểm đối thủ"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent cell editing
            }
        };

        JTable historyTable = new JTable(model);
        historyTable.setRowHeight(40); // Chiều cao hàng mới
        historyTable.setFont(pixelFont);
        historyTable.setOpaque(false);
        historyTable.setBackground(new Color(255, 255, 255, 128));
        historyTable.setGridColor(Color.WHITE);

        // Đặt chiều rộng cột
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50); // Cột STT
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Cột Thời gian
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Cột Điểm
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(50); // Cột Kết quả
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Cột Đối thủ
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Cột Điểm đối thủ

        historyTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        historyTable.getTableHeader().setForeground(Color.BLACK);
        historyTable.getTableHeader().setBackground(new Color(255, 255, 255));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(pixelFont.deriveFont(Font.BOLD, 12f));
                label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.WHITE));
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

        // Fetch player history with opponent information
        List<PlayerGame> historyList = historyController.getPlayerHistory(playerId);
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (PlayerGame game : historyList) {
            String OpponentPlayerID = historyController.getOpponentPlayerID(game.getPlayerID(), game.getGameID());
            String OpponentAccountID = historyController.getOpponentAccountID(OpponentPlayerID);
            String OpponentName = historyController.getOpponentName(OpponentAccountID);
            int OpponentScore = historyController.getOpponentScore(OpponentPlayerID, game.getGameID());
            String formattedJoinTime = sdf.format(game.getJoinTime());

            // Load the corresponding image based on the result
            ImageIcon resultIcon;
            if ("win".equalsIgnoreCase(game.getResult())) {
                resultIcon = new ImageIcon(getClass().getClassLoader().getResource("Client/assets/victory.png"));
            } else if ("lose".equalsIgnoreCase(game.getResult())) {
                resultIcon = new ImageIcon(getClass().getClassLoader().getResource("Client/assets/defeat.png"));
            } else {
                resultIcon = new ImageIcon(getClass().getClassLoader().getResource("Client/assets/draw.png"));
            }
            Image scaledIcon = resultIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Kích thước icon mới
            resultIcon = new ImageIcon(scaledIcon);
            model.addRow(new Object[]{
                    stt++,
                    formattedJoinTime,
                    game.getScore(),
                    resultIcon,
                    OpponentName,
                    OpponentScore,
            });
        }

        // Change the column class of the "Kết quả" column to ImageIcon
        historyTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Fill empty rows if necessary
        int maxRows = 9;
        int currentRows = historyList.size();
        for (int i = currentRows; i < maxRows; i++) {
            model.addRow(new Object[]{
                    "",
                    "",
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
        JLabel titleLabel = new JLabel("Lịch sử chơi", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(7, 0, 15, 0));
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

    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Lỗi khi tải hình ảnh từ URL: " + e.getMessage()); // Error message in Vietnamese
            return null;
        }
    }
}

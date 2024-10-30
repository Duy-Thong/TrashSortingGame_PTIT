package Client.view;

import Client.controller.InviteController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class InviteScreen extends JFrame {
    private InviteController inviteController;
    private JTable playerTable;
    private DefaultTableModel model;
    private JButton inviteButton;
    private JButton backButton;
    private JButton refreshButton;
    private String currentPlayerID;
    private JPanel tablePanel;
    static Font pixelFont;
    private Timer refreshTimer;

    private final String[] columnNames = {"ID", "Tên người chơi", "Tổng điểm", "Trạng thái"};

    public InviteScreen(String playerID, String username) {
        this.inviteController = new InviteController(username);
        this.currentPlayerID = playerID;

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

        setTitle("Mời bạn bè");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby1.jpg")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(model);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerTable.setRowHeight(25);
        playerTable.setFont(pixelFont);
        playerTable.setBackground(new Color(255, 255, 255, 128));
        playerTable.setGridColor(Color.WHITE);

        playerTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        playerTable.getTableHeader().setForeground(Color.BLACK);
        playerTable.getTableHeader().setBackground(new Color(255, 255, 255)); // nếu muốn nền trắng

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

        for (int i = 0; i < playerTable.getColumnModel().getColumnCount(); i++) {
            playerTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnNames.length; i++) {
            playerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new LineBorder(Color.WHITE, 2));

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 55, 10, 55));
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(tablePanel, BorderLayout.CENTER);

        backgroundPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(backgroundPanel);

        loadListFriends();

        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);

        backButton.addActionListener(e -> {
            dispose();
            new LobbyScreen(playerID, username);
        });
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 55, 14, 0));

        JLabel titleLabel = new JLabel("Danh sách bạn bè", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Client/assets/refresh.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH); // 35x35 là kích thước mới
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        refreshButton = new JButton(resizedIcon);
        refreshButton.setPreferredSize(new Dimension(25, 25));
        refreshButton.setToolTipText("Làm mới danh sách bạn bè");
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(false);

        JPanel refreshPanel = new JPanel(new BorderLayout());
        refreshPanel.add(refreshButton, BorderLayout.EAST);
        refreshPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 125));
        refreshPanel.setOpaque(false);
        titlePanel.add(refreshPanel, BorderLayout.EAST);

        refreshButton.addActionListener(e -> {
            loadListFriends();
            JOptionPane.showMessageDialog(this, "Đã làm mới danh sách bạn bè.");
        });

        titlePanel.setOpaque(false);
        return titlePanel;
    }

    private JPanel createButtonPanel() {
        inviteButton = new JButton("Mời");
        inviteButton.setPreferredSize(new Dimension(150, 40));
        inviteButton.setFont(pixelFont.deriveFont(12f));
        inviteButton.setBackground(new Color(0, 123, 255));
        inviteButton.setForeground(Color.WHITE);
        inviteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(pixelFont.deriveFont(12f));
        backButton.setBackground(new Color(204, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // Align buttons to the center with 30px horizontal gap
        buttonPanel.add(inviteButton);
        buttonPanel.add(backButton);
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Add 10px padding to the bottom

        inviteButton.addActionListener(e -> {
            int selectedRow = playerTable.getSelectedRow();
            if (selectedRow != -1) {
                String playerID = playerTable.getValueAt(selectedRow, 0).toString();
                String playerName = playerTable.getValueAt(selectedRow, 1).toString();

                inviteButton.setEnabled(false);

                inviteController.invitePlayer(currentPlayerID, playerID, playerName, new InviteController.InviteCallback() {
                    @Override
                    public void onInviteTimeout(String playerID) {
                        inviteButton.setEnabled(true);
                        JOptionPane.showMessageDialog(InviteScreen.this, "Thời gian mời đã hết, bạn có thể mời lại người chơi.");
                    }

                    @Override
                    public void onInviteAccepted(String playerID, String roomId) {
                        inviteButton.setEnabled(true);
                        dispose();
                        // Chuyển sang màn game
                        new RunGame(roomId,currentPlayerID,playerID).setVisible(true);
                    }

                    @Override
                    public void onInviteDeclined(String playerID) {
                        inviteButton.setEnabled(true);
                        JOptionPane.showMessageDialog(InviteScreen.this, "Người chơi " + playerName + " đã từ chối lời mời.");
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn người chơi để mời.");
            }
        });

        return buttonPanel;
    }

    private void loadListFriends() {
        // Xóa bảng hiện tại ra khỏi `tablePanel`
        tablePanel.removeAll();

        // Tạo lại bảng và thanh cuộn mới
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(model);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerTable.setRowHeight(25);
        playerTable.setFont(pixelFont);
        playerTable.setBackground(new Color(255, 255, 255, 128));
        playerTable.setGridColor(Color.WHITE);

        playerTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));
        playerTable.getTableHeader().setForeground(Color.BLACK);
        playerTable.getTableHeader().setBackground(new Color(255, 255, 255)); // nếu muốn nền trắng

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

        for (int i = 0; i < playerTable.getColumnModel().getColumnCount(); i++) {
            playerTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        playerTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        playerTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        playerTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new LineBorder(Color.WHITE, 2));

        // Thêm lại bảng mới vào `tablePanel`
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm dữ liệu mới vào bảng
        inviteController.getListFriends(currentPlayerID, new InviteController.AvailablePlayersCallback() {
            @Override
            public void onAvailablePlayersReceived(List<Player> players) {
                int cnt = 0;
                for (Player player : players) {
                    cnt++;
                    model.addRow(new Object[]{
                            player.getPlayerID(),
                            player.getUsername(),
                            player.getTotalScore(),
                            "Online"
                    });
                }

                int maxRows = 14;
                for (int i = cnt; i < maxRows; i++) {
                    model.addRow(new Object[]{
                            "",
                            "",
                            "",
                            ""
                    });
                }
            }
        });

        // Cập nhật lại giao diện
        tablePanel.revalidate();
        tablePanel.repaint();
        refreshTimer = new Timer(5000, e -> loadListFriends());
        refreshTimer.start();
    }


    private static class BackgroundPanel extends JPanel {
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

package Client.view;

import Client.controller.InviteController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    static Font pixelFont;

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

        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_notext.png")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        JPanel titlePanel = createTitlePanel();
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Tên người chơi", "Tổng điểm", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(model);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerTable.setFont(pixelFont);

        playerTable.getTableHeader().setFont(pixelFont.deriveFont(Font.BOLD, 12f));

        JScrollPane scrollPane = new JScrollPane(playerTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 55, 5, 55));
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

        JLabel titleLabel = new JLabel("Danh sách bạn bè", JLabel.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Client/assets/refresh.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // 35x35 là kích thước mới
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        refreshButton = new JButton(resizedIcon);
        refreshButton.setPreferredSize(new Dimension(30, 30));
        refreshButton.setToolTipText("Làm mới danh sách bạn bè");
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(false);

        JPanel refreshPanel = new JPanel(new BorderLayout());
        refreshPanel.add(refreshButton, BorderLayout.EAST);
        refreshPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 55));
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
        inviteButton.setBackground(new Color(0, 102, 204));
        inviteButton.setForeground(Color.WHITE);
        inviteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(pixelFont.deriveFont(12f));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Align buttons to the center
        buttonPanel.add(inviteButton);
        buttonPanel.add(backButton);
        buttonPanel.setOpaque(false);

        inviteButton.addActionListener(e -> {
            int selectedRow = playerTable.getSelectedRow();
            if (selectedRow != -1) {
                String playerID = playerTable.getValueAt(selectedRow, 0).toString();
                String playerName = playerTable.getValueAt(selectedRow, 1).toString();

                inviteButton.setEnabled(false);

                inviteController.invitePlayer(currentPlayerID, playerID, playerName, new InviteController.InviteCallback() {
                    @Override
                    public void onInviteTimeout(String playerID) {
                        // Kích hoạt lại nút mời khi hết thời gian chờ
                        inviteButton.setEnabled(true);
                        JOptionPane.showMessageDialog(InviteScreen.this, "Thời gian mời đã hết, bạn có thể mời lại người chơi.");
                    }

                    @Override
                    public void onInviteAccepted(String playerID, String roomId) {
                        // Xử lý khi lời mời được chấp nhận
                        inviteButton.setEnabled(true); // Kích hoạt lại nút mời
                        JOptionPane.showMessageDialog(InviteScreen.this, "Người chơi " + playerName + " đã chấp nhận lời mời!");
                        dispose();
                        // Chuyển sang màn game
                        new RunGame(currentPlayerID, roomId).setVisible(true);
                    }

                    @Override
                    public void onInviteDeclined(String playerID) {
                        inviteButton.setEnabled(true); // Kích hoạt lại nút mời
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
        inviteController.getListFriends(currentPlayerID, new InviteController.AvailablePlayersCallback() {
            @Override
            public void onAvailablePlayersReceived(List<Player> players) {
                model.setRowCount(0);
                for (Player player : players) {
                    model.addRow(new Object[]{
                            player.getPlayerID(),
                            player.getUsername(),
                            player.getTotalScore(),
                            "Online"
                    });
                }
            }
        });
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
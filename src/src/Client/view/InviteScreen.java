package Client.view;

import Client.controller.InviteController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InviteScreen extends JFrame {
    private InviteController inviteController;
    private JTable playerTable;
    private DefaultTableModel model;
    private JButton inviteButton;
    private JButton backButton;
    private JButton refreshButton; // Declare the refresh button
    private String currentPlayerID; // Add currentPlayerID field

    public InviteScreen(String playerID, String username) {
        this.inviteController = new InviteController(username);
        this.currentPlayerID = playerID; // Initialize currentPlayerID

        setTitle("Mời bạn bè");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tạo tiêu đề và nút làm mới
        add(createTitlePanel(), BorderLayout.NORTH);

        // Tạo bảng người chơi có thể mời
        String[] columnNames = {"ID", "Tên người chơi", "Tổng điểm", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa ô trong bảng
            }
        };

        playerTable = new JTable(model);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tạo panel cho bảng
        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.setPreferredSize(new Dimension(780, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Tạo panel cho nút mời và nút trở về
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Lấy danh sách người chơi có status = 1 và isPlaying = 0
        loadListFriends();

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Tạo panel cho tiêu đề và nút "Làm mới"
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());

        // Tạo tiêu đề
        JLabel titleLabel = new JLabel("Danh sách bạn bè", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Thêm tiêu đề vào giữa panel
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Tạo nút "Làm mới" với hình ảnh
        refreshButton = new JButton(new ImageIcon(getClass().getResource("/Client/assets/refresh.png"))); // Thay đổi đường dẫn tới hình ảnh
        refreshButton.setPreferredSize(new Dimension(40, 40)); // Tùy chỉnh kích thước nút
        refreshButton.setToolTipText("Làm mới danh sách bạn bè"); // Thiết lập tooltip cho nút refresh
        refreshButton.setBorderPainted(false); // Xóa đường viền
        refreshButton.setContentAreaFilled(false); // Không tô màu nền
        refreshButton.setFocusPainted(false); // Không tô màu viền khi nút được chọn

        // Panel cho nút refresh
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.add(refreshButton); // Thêm nút refresh vào panel

        // Kết hợp tiêu đề và nút refresh
        titlePanel.add(refreshPanel, BorderLayout.EAST);

        // Action listener cho nút Refresh
        refreshButton.addActionListener(e -> {
            loadListFriends(); // Tải lại danh sách bạn bè khi nút refresh được nhấn
            JOptionPane.showMessageDialog(this, "Đã làm mới danh sách bạn bè."); // Hiển thị popup sau khi làm mới
        });

        return titlePanel;
    }

    // Tạo panel cho nút "Mời" và "Trở về"
    private JPanel createButtonPanel() {
        inviteButton = new JButton("Mời");
        inviteButton.setPreferredSize(new Dimension(150, 40));

        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Align buttons to the center
        buttonPanel.add(inviteButton);
        buttonPanel.add(backButton);

        inviteButton.addActionListener(e -> {
            int selectedRow = playerTable.getSelectedRow();
            if (selectedRow != -1) {
                String playerID = playerTable.getValueAt(selectedRow, 0).toString();
                String playerName = playerTable.getValueAt(selectedRow, 1).toString();

                // Ẩn nút mời
                inviteButton.setEnabled(false);

                // Gọi phương thức invitePlayer trong controller
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

        // Action listener cho nút Trở về
        backButton.addActionListener(e -> {
            dispose();
        });

        return buttonPanel;
    }

    // Tải danh sách người chơi có sẵn
    private void loadListFriends() {
        inviteController.getListFriends(currentPlayerID, new InviteController.AvailablePlayersCallback() {
            @Override
            public void onAvailablePlayersReceived(List<Player> players) {
                model.setRowCount(0); // Xóa các hàng hiện tại
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
}

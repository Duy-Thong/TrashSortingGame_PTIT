package Client.view;

import Client.controller.InviteController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InviteScreen extends JFrame {
    private InviteController inviteController;
    private JTable playerTable;
    private DefaultTableModel model;
    private JButton inviteButton;
    private JButton backButton; // Declare the Back button

    public InviteScreen(String playerID, String username) {
        this.inviteController = new InviteController(username);

        setTitle("Mời bạn bè");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Thêm tiêu đề
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

        // Thêm nút mời và nút trở về
        add(createButtonPanel(playerID), BorderLayout.SOUTH);

        // Lấy danh sách người chơi có status = 1 và isPlaying = 0
        loadListFriends(playerID);

        // Tùy chỉnh giao diện
        setSize(810, 540);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Tạo panel cho nút "Mời" và "Trở về"
    private JPanel createButtonPanel(String currentPlayerID) {
        inviteButton = new JButton("Mời");
        inviteButton.setPreferredSize(new Dimension(150, 40));

        backButton = new JButton("Trở về"); // Initialize Back button
        backButton.setPreferredSize(new Dimension(150, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(inviteButton);
        buttonPanel.add(backButton); // Add Back button to the panel

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
                    public void onInviteAccepted(String playerID) {
                        // Xử lý khi lời mời được chấp nhận
                        inviteButton.setEnabled(true); // Kích hoạt lại nút mời
                        JOptionPane.showMessageDialog(InviteScreen.this, "Người chơi đã chấp nhận lời mời!");
                    }

                    @Override
                    public void onInviteDeclined(String playerID) {
                        inviteButton.setEnabled(true); // Kích hoạt lại nút mời
                        JOptionPane.showMessageDialog(InviteScreen.this, "Người chơi đã từ chối lời mời.");
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

    // Tạo panel tiêu đề
    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Danh sách bạn bè", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    // Tải danh sách người chơi có sẵn
    private void loadListFriends(String playerID) {
        inviteController.getListFriends(playerID, new InviteController.AvailablePlayersCallback() {
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

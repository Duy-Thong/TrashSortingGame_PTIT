package Client.view;

import Client.controller.ProfileController;
import Client.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class ProfileScreen extends JFrame {
    private JButton backButton;
    private JButton editButton;
    private String playerID;
    private ProfileController profileController;
    Font pixelFont;

    public ProfileScreen(String playerID, String username) {
        this.playerID = playerID;
        this.profileController = new ProfileController();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("Client/assets/FVF.ttf");
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pixelFont);
            } else {
                System.out.println("Tệp phông chữ không được tìm thấy.");
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        setTitle("Thông tin cá nhân");
        setSize(810, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sử dụng BackgroundPanel cho ảnh nền
        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby.gif")).getImage());
        backgroundPanel.setLayout(new BorderLayout());

        createBackButton();
        createEditButton();
        JPanel titlePanel = createTitlePanel();
        JPanel infoPanel = createInfoPanel();

        JPanel buttonPanel = createButtonPanel();

        backgroundPanel.add(titlePanel, BorderLayout.NORTH);
        backgroundPanel.add(infoPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LobbyScreen(playerID, username);
            }
        });
        editButton.addActionListener(e -> showEditDialog());

        setVisible(true);
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

    private void createBackButton() {
        backButton = new JButton("Trở về");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setFont(pixelFont.deriveFont(14f));
        backButton.setToolTipText("Trở về");

        // Đặt màu nền đỏ và màu chữ trắng
        backButton.setBackground(new Color(204, 0, 0)); // Màu đỏ
        backButton.setForeground(Color.WHITE); // Màu chữ trắng
        backButton.setOpaque(true); // Đảm bảo màu nền được hiển thị
    }

    private void createEditButton() {
        editButton = new JButton("Chỉnh sửa");
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setFont(pixelFont.deriveFont(14f));
        editButton.setToolTipText("Chỉnh sửa");

        // Đặt màu nền và màu chữ
        editButton.setBackground(new Color(0, 123, 255)); // Màu xanh tùy chọn
        editButton.setForeground(Color.WHITE); // Màu chữ trắng
        editButton.setOpaque(true); // Đảm bảo màu nền được hiển thị
    }

    private JPanel createTitlePanel() {
        JLabel titleLabel = new JLabel("Thông tin cá nhân", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(pixelFont.deriveFont(Font.BOLD, 18f));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        loadProfileData(infoPanel, gbc);
        return infoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Đảm bảo nút không che khuất ảnh nền
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Tạo khoảng cách 10px ở dưới cùng

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 30); // Khoảng cách 30px bên phải nút Trở về
        gbc.anchor = GridBagConstraints.CENTER;

        // Thêm nút "Chỉnh sửa"
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(editButton, gbc);

        // Thêm nút "Trở về"
        gbc.gridx = 1; // Đặt nó ở cột tiếp theo
        buttonPanel.add(backButton, gbc);

        return buttonPanel;
    }

    private void loadProfileData(JPanel infoPanel, GridBagConstraints gbc) {
        Player player = profileController.getPlayerProfile(playerID);
        if (player != null) {
            addLabelAndValue(infoPanel, gbc, "Tên người chơi:", player.getUsername(), 0);
            addLabelAndValue(infoPanel, gbc, "Tổng số trận:", String.valueOf(player.getTotalGames()), 1);
            addLabelAndValue(infoPanel, gbc, "Tổng bàn thắng:", String.valueOf(player.getTotalWins()), 2);
            addLabelAndValue(infoPanel, gbc, "Tổng điểm:", String.valueOf(player.getTotalScore()), 3);
            addLabelAndValue(infoPanel, gbc, "Điểm trung bình:", String.valueOf(player.getAverageScore()), 4);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createdAtStr = dateFormat.format(player.getCreatedAt());
            addLabelAndValue(infoPanel, gbc, "Ngày tạo:", createdAtStr, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cá nhân.");
        }
    }

    private void addLabelAndValue(JPanel panel, GridBagConstraints gbc, String labelText, String valueText, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel label = new JLabel(labelText);
        label.setFont(pixelFont.deriveFont(12f));
        label.setForeground(Color.WHITE); // Màu chữ trắng
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 150)); // Nền mờ màu đen
        label.setBorder(new EmptyBorder(5, 5, 5, 85)); // Tạo padding cho nhãn
        panel.add(label, gbc);

        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(valueText);
        valueLabel.setFont(pixelFont.deriveFont(12f));
        valueLabel.setForeground(Color.WHITE); // Màu chữ trắng
        valueLabel.setOpaque(true);
        valueLabel.setBackground(new Color(0, 0, 0, 150)); // Nền mờ màu đen
        valueLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Tạo padding cho giá trị
        panel.add(valueLabel, gbc);
    }

    private void showEditDialog() {
        Player player = profileController.getPlayerProfile(playerID);
        if (player == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cá nhân.");
            return;
        }

        // Create a panel with a background image
        BackgroundPanel editPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/pop.gif")).getImage());
        editPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control over positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill the available horizontal space

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Align to the east for label
        JLabel usernameLabel = new JLabel("Tên người dùng:");
        usernameLabel.setForeground(Color.WHITE); // Set label color to white
        usernameLabel.setFont(pixelFont.deriveFont(10f));
        editPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Align to the west for text field
        JTextField usernameField = new JTextField(10); // Set a fixed width for the username field
        usernameField.setText(player.getUsername());
        usernameField.setFont(pixelFont.deriveFont(10f));
        editPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; // Align to the east for label
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setForeground(Color.WHITE); // Set label color to white
        passwordLabel.setFont(pixelFont.deriveFont(10f));
        editPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align to the west for password field
        JPasswordField passwordField = new JPasswordField(10); // Set a fixed width for the password field
        passwordField.setFont(pixelFont.deriveFont(10f));
        editPanel.add(passwordField, gbc);

        // Create buttons with the desired colors and sizes
        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(0, 123, 255)); // Blue color for save button
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(pixelFont.deriveFont(10f));
        saveButton.setPreferredSize(new Dimension(20, 25)); // Set smaller size for save button

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(204, 0, 0)); // Red color for cancel button
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(pixelFont.deriveFont(10f));
        cancelButton.setPreferredSize(new Dimension(20, 25)); // Set smaller size for cancel button

        // Add buttons to the panel
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the save button
        editPanel.add(saveButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the cancel button
        editPanel.add(cancelButton, gbc);

        // Create the dialog
        JDialog dialog = new JDialog(this, "Chỉnh sửa thông tin", true);
        dialog.setContentPane(editPanel);
        dialog.setPreferredSize(new Dimension(350, 200)); // Set size for the dialog
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Add action listeners to the buttons
        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            if (!newUsername.isEmpty()) {
                updatePlayerInfo(newUsername, newPassword);
                dialog.dispose(); // Close the dialog after saving
            } else {
                JOptionPane.showMessageDialog(dialog, "Tên người dùng không được để trống.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose()); // Close dialog without saving

        dialog.setVisible(true);
    }

    private void updatePlayerInfo(String username, String password) {
        String response = profileController.updatePlayerProfile(playerID, username, password);
        if (response.startsWith("success")) {
            JOptionPane.showMessageDialog(this, "Thông tin đã được cập nhật thành công!");
        } else if (response.startsWith("error: username already exists")) {
            JOptionPane.showMessageDialog(this, "Tên người dùng đã được sử dụng, vui lòng chọn tên khác!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật thông tin.");
        }
    }

    public JButton getBackButton() {
        return backButton;
    }
}

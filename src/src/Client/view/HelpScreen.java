package Client.view;

import Client.controller.HelpController;
import Client.model.Bin;
import Client.model.TrashItem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;

public class HelpScreen {
    private JFrame helpFrame;
    private int widthImage = 70;  // Kích thước ảnh mong muốn
    private int heightImage = 70; // Kích thước ảnh mong muốn
    private Map<String, ImageIcon> imageCache = new HashMap<>(); // Bộ nhớ cache cho hình ảnh

    public HelpScreen() {
        helpFrame = new JFrame("Hướng dẫn");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(810, 540);
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setLayout(new BorderLayout());

        // Tải font tùy chỉnh
        Font customFont = loadCustomFont("../assets/FVF.ttf"); // Điều chỉnh đường dẫn tới font

        // Panel hướng dẫn chơi
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BorderLayout());

        JTextArea instructions = new JTextArea();
        instructions.setText("Hướng dẫn chơi:\n\n"
                + "1. Mục tiêu của trò chơi là phân loại rác thành đúng thùng rác.\n"
                + "2. Rác sẽ rơi từ trên xuống, và bạn phải nhấn nút tương ứng để di chuyển rác đến thùng rác phù hợp.\n"
                + "3. Bạn sẽ nhận điểm cho mỗi lần phân loại đúng.\n"
                + "4. Hãy cố gắng đạt điểm cao nhất trước khi thời gian kết thúc!");
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setEditable(false);
        instructions.setFont(customFont.deriveFont(Font.PLAIN, 12));
        instructions.setBorder(new EmptyBorder(10, 20, 10, 20));
        JScrollPane scrollPane = new JScrollPane(instructions);
        instructionPanel.add(scrollPane, BorderLayout.CENTER);
        helpFrame.add(instructionPanel, BorderLayout.NORTH);

        JLabel trashBinLabel = new JLabel("Dưới đây là các loại rác và thùng rác");
        trashBinLabel.setFont(customFont.deriveFont(Font.BOLD, 16));
        trashBinLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helpFrame.add(trashBinLabel, BorderLayout.SOUTH);

        HelpController helpController = new HelpController();
        List<TrashItem> trashData = helpController.fetchTrashItemData();
        List<Bin> binData = helpController.fetchBinData();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel binHeaderPanel = new JPanel();
        binHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel binHeaderName = new JLabel("Tên thùng rác");
        JLabel binHeaderType = new JLabel("Loại thùng rác");
        JLabel binHeaderImage = new JLabel("Hình ảnh");

        binHeaderName.setFont(customFont.deriveFont(Font.BOLD, 14));
        binHeaderType.setFont(customFont.deriveFont(Font.BOLD, 14));
        binHeaderImage.setFont(customFont.deriveFont(Font.BOLD, 14));

        binHeaderPanel.add(binHeaderName);
        binHeaderPanel.add(binHeaderType);
        binHeaderPanel.add(binHeaderImage);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        mainPanel.add(binHeaderPanel, gbc);

        int binRowIndex = 1;
        for (Bin bin : binData) {
            JPanel binItemPanel = new JPanel();
            binItemPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel binNameLabel = new JLabel(bin.getName());
            JLabel binTypeLabel = new JLabel(bin.getType());
            JLabel binImage = new JLabel();

            loadImageAsync(bin.getUrl(), binImage); // Tải ảnh không đồng bộ
            binImage.setPreferredSize(new Dimension(widthImage, heightImage));
            binNameLabel.setFont(customFont.deriveFont(Font.PLAIN, 16));
            binTypeLabel.setFont(customFont.deriveFont(Font.BOLD, 20));

            binItemPanel.add(binNameLabel);
            binItemPanel.add(binTypeLabel);
            binItemPanel.add(binImage);
            gbc.gridy = binRowIndex++;
            mainPanel.add(binItemPanel, gbc);
        }

        JPanel trashHeaderPanel = new JPanel();
        trashHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel trashHeaderName = new JLabel("Tên rác");
        JLabel trashHeaderType = new JLabel("Loại rác");
        JLabel trashHeaderImage = new JLabel("Hình ảnh");

        trashHeaderName.setFont(customFont.deriveFont(Font.BOLD, 14));
        trashHeaderType.setFont(customFont.deriveFont(Font.BOLD, 14));
        trashHeaderImage.setFont(customFont.deriveFont(Font.BOLD, 14));

        trashHeaderPanel.add(trashHeaderName);
        trashHeaderPanel.add(trashHeaderType);
        trashHeaderPanel.add(trashHeaderImage);
        gbc.gridy = binRowIndex;
        mainPanel.add(trashHeaderPanel, gbc);

        int trashRowIndex = binRowIndex + 1;
        for (TrashItem item : trashData) {
            JPanel trashItemPanel = new JPanel();
            trashItemPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel trashNameLabel = new JLabel(item.getName());
            JLabel trashLabel = new JLabel(item.getType());
            JLabel trashImage = new JLabel();

            loadImageAsync(item.getUrl(), trashImage); // Tải ảnh không đồng bộ
            trashImage.setPreferredSize(new Dimension(widthImage, heightImage));
            trashNameLabel.setFont(customFont.deriveFont(Font.PLAIN, 16));
            trashLabel.setFont(customFont.deriveFont(Font.BOLD, 20));

            trashItemPanel.add(trashNameLabel);
            trashItemPanel.add(trashLabel);
            trashItemPanel.add(trashImage);
            gbc.gridy = trashRowIndex++;
            mainPanel.add(trashItemPanel, gbc);
        }

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        helpFrame.add(mainScrollPane, BorderLayout.CENTER);

        JButton btnClose = createButton("Đóng", customFont, new Color(204, 0, 0));
        btnClose.addActionListener(e -> helpFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        helpFrame.add(buttonPanel, BorderLayout.SOUTH);

        helpFrame.setVisible(true);
    }

    private JButton createButton(String text, Font font, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(font.deriveFont(Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private void loadImageAsync(String urlString, JLabel label) {
        if (imageCache.containsKey(urlString)) {
            label.setIcon(imageCache.get(urlString));
        } else {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    return urlToImage(urlString);
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (icon != null) {
                            imageCache.put(urlString, icon);
                            label.setIcon(icon);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image asynchronously: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }

    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error loading image from URL: " + e.getMessage());
            return null;
        }
    }

    private Font loadCustomFont(String fontPath) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            return Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 12);
        }
    }
}

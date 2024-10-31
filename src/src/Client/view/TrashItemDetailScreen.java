package Client.view;

import Client.model.TrashItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TrashItemDetailScreen {
    private JFrame detailFrame;
    private int widthImage = 100;  // Desired width of the image
    private int heightImage = 100; // Desired height of the image
    private Font customFont;

    public TrashItemDetailScreen(TrashItem trashItem) {
        // Initialize JFrame
        detailFrame = new JFrame("Chi tiết rác thải");
        detailFrame.setSize(500, 400); // Adjusted frame size
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setLayout(new BorderLayout());

        // Load custom font
        customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to your font

        // Create main panel for the entire detail view
        JPanel mainPanel = createMainPanel(trashItem);
        detailFrame.add(mainPanel, BorderLayout.CENTER);

        // Create and add close button
        JButton closeButton = createCloseButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding
        detailFrame.add(buttonPanel, BorderLayout.SOUTH);

        detailFrame.setVisible(true);
    }

    private JPanel createMainPanel(TrashItem trashItem) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Create and add image panel
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = urlToImage(trashItem.getUrl()); // Assuming getImageUrl() returns the image URL
        if (imageIcon != null) {
            imageLabel.setIcon(imageIcon);
        }
        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(imagePanel); // Add image panel to the main panel

        // Create and add detail panel
        JPanel detailPanel = createDetailPanel(trashItem);
        JScrollPane scrollPane = new JScrollPane(detailPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane);
        return mainPanel;
    }

    private JPanel createDetailPanel(TrashItem trashItem) {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for better spacing
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add name label
        JLabel nameLabel = new JLabel("Tên: " + trashItem.getName());
        nameLabel.setFont(customFont.deriveFont(Font.BOLD, 14)); // Font size
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align to center
        detailPanel.add(nameLabel);

        // Add a small vertical gap between name and description
        detailPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 5 pixel gap

        // Use JTextArea for description to allow wrapping and resizing
        JTextArea descriptionArea = new JTextArea("Mô tả: " + trashItem.getDescription());
        descriptionArea.setFont(customFont.deriveFont(Font.PLAIN, 12)); // Font size
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false); // Make it non-editable
        descriptionArea.setOpaque(false); // Make it transparent to see the background
        descriptionArea.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        detailPanel.add(descriptionArea);

        return detailPanel;
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(customFont.deriveFont(Font.BOLD, 12)); // Font size
        closeButton.addActionListener(e -> detailFrame.dispose());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button alignment
        return closeButton;
    }

    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage img = ImageIO.read(url);
            // Scale the image while maintaining the aspect ratio
            Image scaledImage = img.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Font loadCustomFont(String fontPath) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            return Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 12); // Fallback to default font
        }
    }
}

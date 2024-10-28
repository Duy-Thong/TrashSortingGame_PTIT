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
import java.util.List;
import javax.imageio.ImageIO;

public class HelpScreen {
    private JFrame helpFrame;
    private int widthImage = 100;  // Specify desired width
    private int heightImage = 100; // Specify desired height

    public HelpScreen() {
        helpFrame = new JFrame("Hướng dẫn");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(810, 540); // Increased height for bin display
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setLayout(new BorderLayout());

        // Load custom font
        Font customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to your font file

        // Instructions panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BorderLayout());

        JTextArea instructions = new JTextArea();
        instructions.setText("Hướng dẫn chơi:\n\n"
                + "1. Mục tiêu của trò chơi là phân loại rác thành đúng thùng rác.\n"
                + "2. Rác sẽ rơi từ trên xuống, và bạn phải nhấn nút tương ứng để chọn thùng rác phù hợp.\n"
                + "3. Bạn sẽ nhận điểm cho mỗi lần phân loại đúng.\n"
                + "4. Nếu bạn phân loại sai, điểm sẽ bị trừ.\n"
                + "5. Hãy cố gắng đạt điểm cao nhất trước khi thời gian kết thúc!");
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setEditable(false);
        instructions.setFont(customFont.deriveFont(Font.PLAIN, 16)); // Set custom font for instructions
        JScrollPane scrollPane = new JScrollPane(instructions);
        instructionPanel.add(scrollPane, BorderLayout.CENTER);
        helpFrame.add(instructionPanel, BorderLayout.NORTH);

        // Add a label for the types of trash and bins
        JLabel trashBinLabel = new JLabel("Dưới đây là các loại rác và thùng rác");
        trashBinLabel.setFont(customFont.deriveFont(Font.BOLD, 16)); // Set custom font for the label
        trashBinLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label
        helpFrame.add(trashBinLabel, BorderLayout.SOUTH); // Add the label to the bottom of the frame

        // Fetch data from the HelpController
        HelpController helpController = new HelpController();
        List<TrashItem> trashData = helpController.fetchTrashItemData();
        List<Bin> binData = helpController.fetchBinData(); // Fetch bin data

        // Create a main panel to hold bin and trash items
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Create a header panel for the bin section
        JPanel binHeaderPanel = new JPanel();
        binHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center header
        JLabel binHeaderName = new JLabel("Tên thùng rác");
        JLabel binHeaderType = new JLabel("Loại thùng rác");
        JLabel binHeaderImage = new JLabel("Hình ảnh");

        binHeaderName.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header
        binHeaderType.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header
        binHeaderImage.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header

        binHeaderPanel.add(binHeaderName); // Header for bin name column
        binHeaderPanel.add(binHeaderType); // Header for bin type column
        binHeaderPanel.add(binHeaderImage); // Header for image column

        gbc.gridx = 0; // Start at column 0
        gbc.gridy = 0; // First row
        gbc.weighty = 0; // No extra vertical space
        mainPanel.add(binHeaderPanel, gbc); // Add header to main panel

        // Create and add bins to the main panel
        int binRowIndex = 1; // Start from row 1 for bins
        for (Bin bin : binData) {
            JPanel binItemPanel = new JPanel();
            binItemPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center bin items

            JLabel binNameLabel = new JLabel(bin.getName()); // Label for bin name
            JLabel binTypeLabel = new JLabel(bin.getType()); // Label for bin type
            JLabel binImage = new JLabel();

            // Load and set bin image using urlToImage
            binImage.setIcon(urlToImage(bin.getUrl()));
            binImage.setPreferredSize(new Dimension(widthImage, heightImage)); // Set preferred size
            binNameLabel.setFont(customFont.deriveFont(Font.PLAIN, 16)); // Set custom font for bin name
            binTypeLabel.setFont(customFont.deriveFont(Font.BOLD, 20)); // Set custom font for bin type

            // Add bin name and type to the item panel
            binItemPanel.add(binNameLabel); // Add bin name label
            binItemPanel.add(binTypeLabel); // Add bin type label
            binItemPanel.add(binImage); // Add bin image
            gbc.gridy = binRowIndex++; // Move to next row
            mainPanel.add(binItemPanel, gbc); // Add to main panel
        }

        // Create a header panel for the trash section
        JPanel trashHeaderPanel = new JPanel();
        trashHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center header
        JLabel trashHeaderName = new JLabel("Tên rác");
        JLabel trashHeaderType = new JLabel("Loại rác");
        JLabel trashHeaderImage = new JLabel("Hình ảnh");

        trashHeaderName.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header
        trashHeaderType.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header
        trashHeaderImage.setFont(customFont.deriveFont(Font.BOLD, 14)); // Custom font for header

        trashHeaderPanel.add(trashHeaderName);  // Header for trash name column
        trashHeaderPanel.add(trashHeaderType);  // Header for trash type column
        trashHeaderPanel.add(trashHeaderImage);   // Header for image column
        gbc.gridy = binRowIndex; // Set row for trash header
        mainPanel.add(trashHeaderPanel, gbc); // Add header to main panel

        // Create and add trash items to the main panel
        int trashRowIndex = binRowIndex + 1; // Start from the next row after headers
        for (TrashItem item : trashData) {
            JPanel trashItemPanel = new JPanel();
            trashItemPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center trash items

            JLabel trashNameLabel = new JLabel(item.getName()); // Label for trash name
            JLabel trashLabel = new JLabel(item.getType()); // Label for trash type
            JLabel trashImage = new JLabel();

            // Load and set trash item image using urlToImage
            trashImage.setIcon(urlToImage(item.getUrl()));
            trashImage.setPreferredSize(new Dimension(widthImage, heightImage)); // Set preferred size
            trashNameLabel.setFont(customFont.deriveFont(Font.PLAIN, 16)); // Set custom font for trash name
            trashLabel.setFont(customFont.deriveFont(Font.BOLD, 20)); // Set custom font for trash type

            // Add trash name and type to the item panel
            trashItemPanel.add(trashNameLabel); // Add trash name label
            trashItemPanel.add(trashLabel); // Add trash type label
            trashItemPanel.add(trashImage); // Add image
            gbc.gridy = trashRowIndex++; // Move to next row
            mainPanel.add(trashItemPanel, gbc); // Add to main panel
        }

        // Wrap the main panel in a scroll pane
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        helpFrame.add(mainScrollPane, BorderLayout.CENTER); // Add scrollable main panel

        // Create a Close button at the bottom
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(customFont.deriveFont(Font.BOLD, 14)); // Set custom font for Close button
        btnClose.addActionListener(e -> helpFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        helpFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Set frame visibility
        helpFrame.setVisible(true);
    }

    private ImageIcon urlToImage(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage originalImage = ImageIO.read(url);
            Image scaledImage = originalImage.getScaledInstance(widthImage, heightImage, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            System.err.println("Error loading image from URL: " + e.getMessage());
            return null; // Handle loading failure
        }
    }

    private Font loadCustomFont(String fontPath) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            return Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 12); // Fallback to default font
        }
    }


}

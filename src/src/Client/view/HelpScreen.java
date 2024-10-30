package Client.view;

import Client.controller.HelpController;
import Client.model.Bin;
import Client.model.TrashItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpScreen {
    private JFrame helpFrame;
    private int widthImage = 70;  // Desired image size
    private int heightImage = 70; // Desired image size
    private Map<String, ImageIcon> imageCache = new HashMap<>(); // Image cache
    private Font customFont;

    public HelpScreen() {
        helpFrame = new JFrame("Hướng dẫn");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(810, 540);
        helpFrame.setLocationRelativeTo(null);
        helpFrame.setLayout(new BorderLayout());

        // Load custom font
        customFont = loadCustomFont("../assets/FVF.ttf"); // Adjust the path to the font

        // Create a background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel(new ImageIcon(getClass().getClassLoader().getResource("Client/assets/back_lobby1.jpg")).getImage());
        backgroundPanel.setLayout(new BorderLayout());
        helpFrame.add(backgroundPanel);

        // Instructions panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setOpaque(false); // Set to transparent
        instructionPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        JLabel titleLabel = new JLabel("Hướng Dẫn Chơi");
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE); // Title color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionPanel.add(titleLabel);

        JTextArea instructions = new JTextArea();
        instructions.setText("Mục tiêu của trò chơi là phân loại rác thành đúng thùng rác.\n"
                + "Rác sẽ rơi từ trên xuống, và bạn phải nhấn nút tương ứng để di chuyển rác đến thùng rác phù hợp.\n"
                + "Bạn sẽ nhận điểm cho mỗi lần phân loại đúng.\n"
                + "Hãy cố gắng đạt điểm cao nhất trước khi thời gian kết thúc!");
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setEditable(false);
        instructions.setFont(customFont.deriveFont(Font.PLAIN, 14));
        instructions.setForeground(Color.BLACK); // Set text color
        instructions.setBackground(new Color(255, 255, 255, 100)); // Semi-transparent background with alpha 100
        instructions.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(instructions);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(760, 150)); // Adjusted height for scrolling
        instructionPanel.add(scrollPane);

        // Add the instruction panel to the background
        backgroundPanel.add(instructionPanel, BorderLayout.NORTH);

        // Load bin and trash data
        HelpController helpController = new HelpController();
        List<TrashItem> trashData = helpController.fetchTrashItemData();
        List<Bin> binData = helpController.fetchBinData();

        // Combined Table to hold both bins and trash items
        String[] columns = {"Tên", "Loại", "Loại đối tượng", "Hình ảnh"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent cell editing
            }
        };
        JTable combinedTable = new JTable(tableModel);
        combinedTable.setRowHeight(heightImage + 10); // Set row height for images
        combinedTable.setFont(customFont.deriveFont(Font.PLAIN, 12));
        combinedTable.setOpaque(false); // Set table to be transparent
        combinedTable.setBackground(new Color(255, 255, 255, 100)); // Semi-transparent background
        combinedTable.setForeground(Color.BLACK); // Set text color to black for better visibility

        JTableHeader tableHeader = combinedTable.getTableHeader();
        tableHeader.setFont(customFont.deriveFont(Font.BOLD, 14)); // Apply custom font to header
        tableHeader.setForeground(Color.BLACK); // Set header text color
        tableHeader.setBackground(new Color(255, 255, 255, 100)); // Set header background color

        // Centering text in the table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            combinedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Populate combined table with bins
        for (Bin bin : binData) {
            tableModel.addRow(new Object[]{bin.getName(), bin.getType(), "Thùng rác", null});
            loadImageAsync(bin.getUrl(), combinedTable, tableModel.getRowCount() - 1, 3);
        }

        // Populate combined table with trash items
        for (TrashItem item : trashData) {
            tableModel.addRow(new Object[]{item.getName(), item.getType(), "Rác", null});
            loadImageAsync(item.getUrl(), combinedTable, tableModel.getRowCount() - 1, 3);
        }

        // Change the column class of the "Hình ảnh" column to ImageIcon
        combinedTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                }
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true); // Ensure label is opaque
                label.setBackground(new Color(255, 255, 255, 100)); // Semi-transparent background
                label.setFont(customFont.deriveFont(Font.PLAIN, 12)); // Set custom font
                return label;
            }
        });

        // Add combined scroll pane to the main frame
        JScrollPane combinedScrollPane = new JScrollPane(combinedTable);
        combinedScrollPane.setOpaque(false);
        combinedScrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(combinedScrollPane, BorderLayout.CENTER);

        // Close button
        JButton btnClose = createButton("Đóng", customFont, new Color(204, 0, 0));
        btnClose.addActionListener(e -> helpFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Set to transparent
        buttonPanel.add(btnClose);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 100)); // Semi-transparent background with alpha 90
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        helpFrame.setVisible(true);
        helpFrame.setAlwaysOnTop(true); // Keep the window on top if needed
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

    private void loadImageAsync(String urlString, JTable table, int row, int column) {
        if (imageCache.containsKey(urlString)) {
            table.setValueAt(imageCache.get(urlString), row, column);
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
                            table.setValueAt(icon, row, column);
                        } else {
                            table.setValueAt(null, row, column); // Set to null if image fails to load
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image asynchronously: " + e.getMessage());
                        table.setValueAt(null, row, column); // Set to null if there's an error
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
            return new ImageIcon(scaledImage); // Load the image normally
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

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
